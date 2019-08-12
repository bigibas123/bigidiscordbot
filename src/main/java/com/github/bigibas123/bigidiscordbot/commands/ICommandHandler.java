package com.github.bigibas123.bigidiscordbot.commands;

import net.dv8tion.jda.core.entities.Message;

public interface ICommandHandler {
    boolean handleCommand(Message message);
}
