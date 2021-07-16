package com.github.bigibas123.bigidiscordbot.commands.general;

import com.github.bigibas123.bigidiscordbot.Reference;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.internal.requests.CallbackContext;

import java.util.Collection;
import java.util.List;

public class ExitCommand extends ICommand {
    public ExitCommand() {
        super("Exit", "Stops the bot", "", "end", "quit");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        replyContext.getChannel().sendMessage(Emoji.WAVE.s()).complete();
        replyContext.getJDA().shutdown();
        System.exit(0);
        return true;
    }

    @Override
    public boolean hasPermission(User user, Member member, MessageChannel channel) {
        return Reference.ownerID.equals(user.getId());
    }

    @Override
    protected CommandData _getCommandData(CommandData c) {
        return c.setDefaultEnabled(false);
    }
    @Override
    protected Collection<? extends CommandPrivilege> _getPrivilegesForGuild(Guild g, List<Role> roles, List<CommandPrivilege> list) {
        CallbackContext.getInstance().close();
        Member owner = g.retrieveMemberById(Reference.ownerID).complete();
        if(owner != null){
            list.add(CommandPrivilege.enable(owner.getUser()));
        }
        return list;
    }
}
