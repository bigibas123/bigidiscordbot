package com.github.bigibas123.bigidiscordbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class StopCommand extends ICommand {
    public StopCommand() {
        super("stop", "exit", "end", "quit");
    }

    @Override
    public boolean execute(Message message, String... args) {
        message.addReaction("\uD83D\uDC4B").complete();
        message.getJDA().shutdown();
        System.exit(0);
        return true;
    }

    @Override
    public boolean hasPermission(User user) {
        return user.getIdLong() == 166980806877642752L;
    }
}
