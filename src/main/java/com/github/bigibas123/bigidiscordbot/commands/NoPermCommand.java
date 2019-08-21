package com.github.bigibas123.bigidiscordbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class NoPermCommand extends ICommand {
    public NoPermCommand() {
        super("noPerm", "**TESTCOMMAND** Nobody is allowed to run this", "", "noPermTest", "stopMe");
    }

    @Override
    public boolean execute(Message message, String... args) {
        return false;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return false;
    }
}
