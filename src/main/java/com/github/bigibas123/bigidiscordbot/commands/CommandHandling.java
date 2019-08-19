package com.github.bigibas123.bigidiscordbot.commands;

import com.github.bigibas123.bigidiscordbot.util.ReactionSheduler;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;

import static com.github.bigibas123.bigidiscordbot.util.Emoji.*;

public class CommandHandling {

    private static HashMap<String, ICommand> commands = new HashMap<>();

    static {
        registerCommand(new StopCommand());
        registerCommand(new NoPermCommand());
        registerCommand(new LongRunningCommand());
    }

    public static void registerCommand(ICommand cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
        for (String alias : cmd.getAliases()) {
            commands.put(alias.toLowerCase(), cmd);
        }
    }

    public CommandHandling() {

    }

    public boolean handleCommand(Message message) {
        String[] msg = message.getContentRaw().split(" ");
        if (message.isMentioned(message.getJDA().getSelfUser(), Message.MentionType.USER)) {
            if (msg.length > 1) {
                ICommand cmd;
                if ((cmd = commands.get(msg[1].toLowerCase())) != null && cmd.hasPermission(message.getAuthor())) {
                    message.addReaction(STOP_WATCH.s()).queue();
                    boolean cmdSuccess = cmd.execute(message, msg);
                    ReactionSheduler.sheduleRemoval(message.getIdLong(), STOP_WATCH.s());
                    if (cmdSuccess) {
                        message.addReaction(CHECK_MARK.s()).queue();
                        return true;
                    } else {
                        message.addReaction(CROSS.s()).queue();
                        return false;
                    }

                } else {
                    if (cmd != null && !cmd.hasPermission(message.getAuthor())) {
                        message.addReaction(STOP_SIGN.s()).queue();
                    } else {
                        message.addReaction(SHRUG.s()).queue();
                    }

                }
            }
        }
        return false;
    }
}
