package com.github.bigibas123.bigidiscordbot.commands;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.general.ExitCommand;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.commands.moderation.Prune;
import com.github.bigibas123.bigidiscordbot.commands.music.*;
import com.github.bigibas123.bigidiscordbot.commands.testing.LongRunningCommand;
import com.github.bigibas123.bigidiscordbot.commands.testing.NoPermCommand;
import com.github.bigibas123.bigidiscordbot.util.ReactionScheduler;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.internal.requests.CallbackContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.github.bigibas123.bigidiscordbot.util.Emoji.*;

public class CommandHandling {

    private static final ArrayList<ICommand> helpList = new ArrayList<>();
    private static final HashMap<String, ICommand> commands = new HashMap<>();

    static {
        registerCommand(new HelpCommand());
        registerCommand(new ExitCommand());
        registerCommand(new Prune());
        registerCommand(new NoPermCommand());
        registerCommand(new LongRunningCommand());
        registerCommand(new PlayCommand());
        registerCommand(new StopCommand());
        registerCommand(new SkipCommand());
        registerCommand(new QueueCommand());
        registerCommand(new SeekCommand());
        registerCommand(new SwapCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new VolumeCommand());
        registerCommand(new PauseCommand());
    }

    public static void registerCommand(ICommand cmd) {
        helpList.add(cmd);
        commands.put(cmd.getName().toLowerCase(), cmd);
        for (String alias: cmd.getAliases()) {
            commands.put(alias.toLowerCase(), cmd);
        }
    }

    public static ArrayList<ICommand> getHelpList() {
        return helpList;
    }

    public static HashMap<String, ICommand> getCommandList() {
        return commands;
    }

    public CommandHandling() {

    }

    public static void handleSlashRegistration(ReadyEvent event) {

//        event.getJDA().retrieveCommands().queue(s -> {
//            s.forEach(cmd -> {
//                event.getJDA().deleteCommandById(cmd.getIdLong()).queue();
//            });
//        });
        event.getJDA().updateCommands().addCommands(
            getHelpList().stream()
                .map(ICommand::getCommandData)
                .collect(Collectors.toList())
        ).queue();
//        Guild debugGuild = event.getJDA().getGuildById(232516313099141121L);
//        assert debugGuild != null;
//        debugGuild.retrieveCommands()
//           .queue(s ->
//               s.forEach(cmd -> debugGuild
//                   .deleteCommandById(cmd.getIdLong())
//                   .queue()
//               )
//           );

        //send commands to testguild for rapid testing
//         event.getJDA().getGuildById(232516313099141121L).updateCommands().addCommands(
//             getHelpList().stream()
//                 .map(ICommand::getCommandData)
//                 .collect(Collectors.toList())
//         ).queue();
    }

    public void handleSlashCommand(@NotNull SlashCommandEvent event) {
        new Thread(() -> {
            CallbackContext.getInstance().close();
            ReplyContext rc = new ReplyContext(event);
            ICommand cmd = commands.get(rc.getSCmdEvent().getName());
            if (cmd != null && cmd.hasPermission(rc.getUser(), rc.getMember(), rc.getChannel())) {
                rc.reply(STOP_WATCH);
                try {
                    //TODO fix all commands to take a map or something
                    boolean cmdSuccess = cmd.execute(rc);
                    if (cmdSuccess) {
                        rc.reply(CHECK_MARK);
                        Main.log.trace(String.format("User: %s executed %s successfully", rc.getUser(), cmd.getName()));
                    } else {
                        rc.reply(CROSS);
                        Main.log.trace(String.format("User: %s executed %s unsuccessfully", rc.getUser(), cmd.getName()));
                    }
                } catch (Throwable e) {
                    rc.reply(WARNING);
                    Main.log.error("Command failed:", e);
                }
            } else {
                if (cmd != null && !cmd.hasPermission(rc.getUser(), rc.getMember(), rc.getChannel())) {
                    rc.reply(STOP_SIGN);
                    Main.log.debug(String.format("User: %s got permission denied for %s", rc.getUser(), cmd.getName()));
                } else {
                    Main.log.trace(String.format("User: %s tried to execute: %s but not found", rc.getUser(), rc.getSCmdEvent().getName()));
                    rc.reply(SHRUG);
                }

            }
        }).start();
    }

    public void handleCommand(Message message) {
        new Thread(() -> {
            CallbackContext.getInstance().close();
            String[] msg = message.getContentRaw().split(" ");
            ReplyContext rc = new ReplyContext(message);
            if (Utils.mentionsMe(message)) {
                if (msg.length > 1) {
                    ICommand cmd = commands.get(msg[1].toLowerCase());
                    if (cmd != null && cmd.hasPermission(message.getAuthor(), rc.getMember(), message.getChannel())) {
                        rc.reply(STOP_WATCH);
                        try {
                            boolean cmdSuccess = cmd.execute(rc, msg);
                            if (cmdSuccess) {
                                rc.reply(CHECK_MARK);
                                Main.log.trace(String.format("User: %s executed %s successfully", message.getAuthor(), cmd.getName()));
                            } else {
                                rc.reply(CROSS);
                                Main.log.trace(String.format("User: %s executed %s unsuccessfully", message.getAuthor(), cmd.getName()));
                            }
                            ReactionScheduler.scheduleRemoval(message.getIdLong(), STOP_WATCH.s());
                        } catch (Throwable e) {
                            rc.reply(WARNING);
                            Main.log.error("Command failed:", e);
                        }
                    } else {
                        if (cmd != null && !cmd.hasPermission(message.getAuthor(), rc.getMember(), message.getChannel())) {
                            rc.reply(STOP_SIGN);
                            Main.log.debug(String.format("User: %s got permission denied for %s", message.getAuthor(), cmd.getName()));
                        } else {
                            Main.log.trace(String.format("User: %s tried to execute: %s but not found", message.getAuthor(), msg[1]));
                            rc.reply(SHRUG);
                        }

                    }
                }
            }
        }).start();
    }

    @SneakyThrows(InterruptedException.class)
    public void registerSlashCommandPerms(Guild guild) {
        Map<String, Collection<? extends CommandPrivilege>> map = new HashMap<>();
        var cdl = new CountDownLatch(2);
        guild.getJDA().retrieveCommands().queue(s -> {
            s.forEach(cmd -> map.put(cmd.getId(), getCommandList().get(cmd.getName()).getPrivileges(guild)));
            cdl.countDown();
        });
        guild.retrieveCommands().queue(s -> {
            s.forEach(cmd -> map.put(cmd.getId(), getCommandList().get(cmd.getName()).getPrivileges(guild)));
            cdl.countDown();
        });
        cdl.await();
        guild.updateCommandPrivileges(map).queue(
            suc -> Main.log.info("Updated commandPriveleges for: "+guild.getIdLong()),
            err -> Main.log.error("Error updating command privilegees for: "+guild.getIdLong(),err)
        );
    }

}
