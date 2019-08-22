package com.github.bigibas123.bigidiscordbot.commands.general;

import com.github.bigibas123.bigidiscordbot.Reference;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class ExitCommand extends ICommand {
    public ExitCommand() {
        super("Exit", "Stops the bot", "", "end", "quit");
    }

    @Override
    public boolean execute(Message message, String... args) {
        message.addReaction(Emoji.WAVE.s()).complete();
        message.getJDA().shutdown();
        System.exit(0);
        return true;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return Reference.ownerID.equals(user.getId());
    }
}
