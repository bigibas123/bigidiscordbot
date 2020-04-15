package com.github.bigibas123.bigidiscordbot.commands;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;


public abstract class ICommand {
    @Getter
    private final String name;
    @Getter
    private final String[] aliases;
    @Getter
    private final String description;
    @Getter
    private final String syntax;

    public ICommand(String name, String description, String syntax, String... aliases) {
        this.name = name;
        if (syntax == null || syntax.equals("")) syntax = " ";
        this.syntax = syntax;
        this.aliases = aliases;
        this.description = description;
    }

    public abstract boolean execute(Message message, String... args);

    public abstract boolean hasPermission(User user, MessageChannel channel);
}
