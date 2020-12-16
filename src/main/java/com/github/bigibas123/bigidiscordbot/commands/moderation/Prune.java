package com.github.bigibas123.bigidiscordbot.commands.moderation;

import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.List;
import java.util.Optional;

public class Prune extends ICommand {

    public Prune() {
        super("Prune", "Removes messages", "[amount def=5]", "Purge", "Delete");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        int amount;
        if (args.length == 2) {
            amount = 5;
        } else {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                replyContext.reply(args[2],"is not a number");
                return false;
            }
        }
        if (replyContext.getChannel() instanceof PrivateChannel) {
            if (amount <= 100) {
                List<Message> hist = replyContext.getChannel().getHistoryBefore(replyContext.getOriginal(),amount).complete().getRetrievedHistory();
                hist.parallelStream()
                        .filter(msg -> Utils.isSameThing(msg.getAuthor(), replyContext.getJDA().getSelfUser()))
                        .forEach(hm -> hm.delete().complete());
            } else {
                while (amount > 0) {
                    List<Message> hist = replyContext.getChannel().getHistoryBefore(replyContext.getOriginal(), Math.min(amount, 100)).complete().getRetrievedHistory();
                    hist.parallelStream()
                            .filter(msg -> Utils.isSameThing(msg.getAuthor(), replyContext.getJDA().getSelfUser()))
                            .forEach(hm -> hm.delete().complete());
                    amount -= 100;
                }
            }
        } else {
            if (amount <= 100) {
                List<Message> hist = replyContext.getChannel().getHistoryBefore(replyContext.getOriginal(),amount).complete().getRetrievedHistory();
                hist.parallelStream()
                        .forEach(hm -> hm.delete().complete());
            } else {
                while (amount > 0) {
                    List<Message> hist = replyContext.getChannel().getHistoryBefore(replyContext.getOriginal(),Math.min(amount, 100)).complete().getRetrievedHistory();
                    hist.parallelStream()
                            .forEach(hm -> hm.delete().complete());
                    amount -= 100;
                }
            }
        }
        return true;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        if (channel instanceof PrivateChannel) {
            return true;
        } else if (channel instanceof TextChannel tc) {
            Member member = tc.getMembers().stream().filter( m -> m.getIdLong()==user.getIdLong()).findFirst().orElse(null);;
            if (member == null) {
                throw new IllegalArgumentException("User:" + user + " does not seem to be a member of:" + channel.getName());
            } else {
                return PermissionUtil.checkPermission(member, Permission.MESSAGE_MANAGE);
            }
        }
        return false;
    }

}

