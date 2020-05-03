package com.github.bigibas123.bigidiscordbot.commands.testing;

import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class NoPermCommand extends ICommand {
    public NoPermCommand() {
        super("noPerm", "**TESTCOMMAND** Nobody is allowed to run this", "", "noPermTest", "stopMe");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        return false;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return false;
    }
}
