package com.github.bigibas123.bigidiscordbot.commands;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.util.ReactionSheduler;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;

import static com.github.bigibas123.bigidiscordbot.util.Emoji.*;

public class CommandHandling {

    private static ArrayList<ICommand> helpList = new ArrayList<>();
    private static HashMap<String, ICommand> commands = new HashMap<>();

    static {
        registerCommand(new HelpCommand(helpList));
        registerCommand(new StopCommand());
        registerCommand(new Prune());
        registerCommand(new NoPermCommand());
        registerCommand(new LongRunningCommand());
    }

    public static void registerCommand(ICommand cmd) {
        helpList.add(cmd);
        commands.put(cmd.getName().toLowerCase(), cmd);
        for (String alias : cmd.getAliases()) {
            commands.put(alias.toLowerCase(), cmd);
        }
    }

    public static ArrayList<ICommand> getHelpList() {
        return helpList;
    }

    public CommandHandling() {

    }

    public boolean handleCommand(Message message) {
        String[] msg = message.getContentRaw().split(" ");
        if (message.isMentioned(message.getJDA().getSelfUser(), Message.MentionType.USER)) {
            if (msg.length > 1) {
                ICommand cmd;
                if ((cmd = commands.get(msg[1].toLowerCase())) != null && cmd.hasPermission(message.getAuthor(), message.getChannel())) {
                    message.addReaction(STOP_WATCH.s()).queue();
                    boolean cmdSuccess = cmd.execute(message, msg);
                    ReactionSheduler.sheduleRemoval(message.getIdLong(), STOP_WATCH.s());
                    if (cmdSuccess) {
                        message.addReaction(CHECK_MARK.s()).queue();
                        Main.log.info(String.format("User: %s executed %s successfully", message.getAuthor().toString(), cmd.getName()));
                        return true;
                    } else {
                        message.addReaction(CROSS.s()).queue();
                        Main.log.info(String.format("User: %s executed %s unsuccessfully", message.getAuthor().toString(), cmd.getName()));
                        return false;
                    }
                } else {
                    if (cmd != null && !cmd.hasPermission(message.getAuthor(), message.getChannel())) {
                        message.addReaction(STOP_SIGN.s()).queue();
                        Main.log.info(String.format("User: %s got permissison denied for %s", message.getAuthor().toString(), cmd.getName()));
                    } else {
                        Main.log.info(String.format("User: %s tried to execute: %s but not found", message.getAuthor().toString(), msg[1]));
                        message.addReaction(SHRUG.s()).queue();
                    }

                }
            }
        }
        return false;
    }
}
