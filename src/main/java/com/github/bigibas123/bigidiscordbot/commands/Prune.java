package com.github.bigibas123.bigidiscordbot.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Optional;

public class Prune extends ICommand {
    public Prune() {
        super("Prune", "Purge", "Delete");
    }

    @Override
    public boolean execute(Message message, String... args) {
        int amount;
        if (args.length == 2) {
            amount = 5;
        } else {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " " + args[2] + " is not a number").queue();
                return false;
            }
        }
        if (message.getChannel() instanceof PrivateChannel) {
            message.getChannel().getHistory().retrievePast(amount).queue(hist -> hist.stream()
                    .filter(msg -> msg.getAuthor().getId().equals(message.getJDA().getSelfUser().getId()))
                    .forEach(hm -> hm.delete().queue())
            );
        } else {
            message.getChannel().getHistory().retrievePast(amount).queue(s -> s.forEach(msg -> msg.delete().queue()));
        }
        return true;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        if (channel instanceof PrivateChannel) {
            return true;
        } else if (channel instanceof TextChannel) {
            Member member;
            Optional<Member> opt = ((TextChannel) channel).getMembers().stream()
                    .filter(m -> m.getUser().getId().equals(user.getId()))
                    .findFirst();
            if (opt.isEmpty()) {
                throw new IllegalArgumentException("User:" + user + " does not seem to be a member of:" + channel.getName());
            } else {
                member = opt.get();
                return PermissionUtil.checkPermission((Channel) channel, member, Permission.MESSAGE_MANAGE);
            }
        }
        return false;
    }
}

