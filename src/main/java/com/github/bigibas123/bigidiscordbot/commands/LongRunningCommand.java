package com.github.bigibas123.bigidiscordbot.commands;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.Reference;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class LongRunningCommand extends ICommand {
    public LongRunningCommand() {
        super("long", "waitCommand", "sleepTest");
    }

    @Override
    public boolean execute(Message message, String... args) {
        try {
            Main.log.info("Tick");
            Thread.sleep(4000);
            Main.log.info("Tock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean hasPermission(User user) {
        return user.getId().equals(Reference.ownerID);
    }
}
