package com.github.bigibas123.bigidiscordbot.commands;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.general.ExitCommand;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.commands.moderation.Prune;
import com.github.bigibas123.bigidiscordbot.commands.music.NowPlayingCommand;
import com.github.bigibas123.bigidiscordbot.commands.music.PauseCommand;
import com.github.bigibas123.bigidiscordbot.commands.music.PlayCommand;
import com.github.bigibas123.bigidiscordbot.commands.music.QueueCommand;
import com.github.bigibas123.bigidiscordbot.commands.music.SeekCommand;
import com.github.bigibas123.bigidiscordbot.commands.music.SkipCommand;
import com.github.bigibas123.bigidiscordbot.commands.music.StopCommand;
import com.github.bigibas123.bigidiscordbot.commands.music.SwapCommand;
import com.github.bigibas123.bigidiscordbot.commands.music.VolumeCommand;
import com.github.bigibas123.bigidiscordbot.commands.testing.LongRunningCommand;
import com.github.bigibas123.bigidiscordbot.commands.testing.NoPermCommand;
import com.github.bigibas123.bigidiscordbot.util.ReactionScheduler;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.requests.CallbackContext;

import java.util.ArrayList;
import java.util.HashMap;

import static com.github.bigibas123.bigidiscordbot.util.Emoji.CHECK_MARK;
import static com.github.bigibas123.bigidiscordbot.util.Emoji.CROSS;
import static com.github.bigibas123.bigidiscordbot.util.Emoji.SHRUG;
import static com.github.bigibas123.bigidiscordbot.util.Emoji.STOP_SIGN;
import static com.github.bigibas123.bigidiscordbot.util.Emoji.STOP_WATCH;
import static com.github.bigibas123.bigidiscordbot.util.Emoji.WARNING;

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
                                Main.log.trace(String.format("User: %s executed %s successfully", message.getAuthor().toString(), cmd.getName()));
                            } else {
                                rc.reply(CROSS);
                                Main.log.trace(String.format("User: %s executed %s unsuccessfully", message.getAuthor().toString(), cmd.getName()));
                            }
                            ReactionScheduler.scheduleRemoval(message.getIdLong(), STOP_WATCH.s());
                        } catch (Throwable e) {
                            rc.reply(WARNING);
                            Main.log.error("Command failed:", e);
                        }
                    } else {
                        if (cmd != null && !cmd.hasPermission(message.getAuthor(), rc.getMember(), message.getChannel())) {
                            rc.reply(STOP_SIGN);
                            Main.log.debug(String.format("User: %s got permission denied for %s", message.getAuthor().toString(), cmd.getName()));
                        } else {
                            Main.log.trace(String.format("User: %s tried to execute: %s but not found", message.getAuthor().toString(), msg[1]));
                            rc.reply(SHRUG);
                        }

                    }
                }
            }
        }).start();
    }

}
