package com.github.bigibas123.bigidiscordbot.commands;

import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;

public class CommandHandling implements ICommandHandler {

    private static HashMap<String, ICommand> commands = new HashMap<>();

    static {
        registerCommand(new StopCommand());
    }

    public static void registerCommand(ICommand cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
        for (String alias : cmd.getAliases()) {
            commands.put(alias.toLowerCase(), cmd);
        }
    }

    public CommandHandling() {

    }

    @Override
    public boolean handleCommand(Message message) {
        String[] msg = message.getContentRaw().split(" ");
        if (message.isMentioned(message.getJDA().getSelfUser(), Message.MentionType.USER)) {
            if (msg.length > 1) {
                ICommand cmd;
                if ((cmd = commands.get(msg[1].toLowerCase())) != null && cmd.hasPermission(message.getAuthor())) {
                    message.addReaction("✔").queue();
                    cmd.execute(message, msg);
                } else {
                    if (cmd != null && !cmd.hasPermission(message.getAuthor())) {
                        message.addReaction("\uD83D\uDED1").queue();
                    }
                    message.addReaction("❌").queue();
                }
            }
        }
        return false;
    }
}
