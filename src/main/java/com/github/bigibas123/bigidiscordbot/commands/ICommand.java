package com.github.bigibas123.bigidiscordbot.commands;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public abstract class ICommand {
    @Getter
    private final String name;
    @Getter
    private final String[] aliases;

    public ICommand(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract boolean execute(Message message, String... args);

    public abstract boolean hasPermission(User user);
}
