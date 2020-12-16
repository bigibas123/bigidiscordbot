package com.github.bigibas123.bigidiscordbot.commands.general;

import com.github.bigibas123.bigidiscordbot.commands.CommandHandling;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class HelpCommand extends ICommand {

    public HelpCommand() {
        super("Help", "Displays this menu", "[command]", "h");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {

        if (args.length == 2) {
            return sendCommandList(replyContext);
        } else if (args.length > 2) {
            return sendCommandDescription(replyContext, args);
        }
        return false;
    }

    public static boolean sendCommandList(ReplyContext rc) {
        EmbedBuilder ebb = new EmbedBuilder();
        ebb.setFooter("Requested by @" + rc.getUser().getName(), rc.getUser().getEffectiveAvatarUrl());
        ebb.setTitle("Help");
        ebb.appendDescription(rc.getJDA().getSelfUser().getAsMention()+" help [command] - for more info");
        ebb.setColor(Color.GREEN);
        StringBuilder names = new StringBuilder();
        StringBuilder descriptions = new StringBuilder();
        boolean first = true;
        for (ICommand command : CommandHandling.getHelpList()) {
            if (!command.hasPermission(rc.getUser(), rc.getMember(), rc.getChannel())) continue;
            if (first) {
                first = false;
                names.append(command.getName());
                descriptions.append(command.getDescription());
            } else {
                names.append("\r\n").append(command.getName());
                descriptions.append("\r\n").append(command.getDescription());
            }
        }
        ebb.addField("Command", names.toString(), true);
        ebb.addField("Description", descriptions.toString(), true);
        rc.getChannel().sendMessage(ebb.build()).queue();
        return true;
    }

    public static boolean sendCommandDescription(ReplyContext message, String... args) {
        EmbedBuilder ebb = new EmbedBuilder();
        ebb.setFooter("Requested by @" + message.getUser().getName(), message.getUser().getEffectiveAvatarUrl());
        ICommand cmd = CommandHandling.getCommandList().get(args[2].toLowerCase());
        if (cmd != null) {
            ebb.setTitle(cmd.getName());
            ebb.setColor(Color.GREEN);
            ebb.appendDescription(cmd.getDescription()).appendDescription("\r\n");
            ebb.addField("Usage", String.format("%s %s %s", message.getJDA().getSelfUser().getAsMention(), cmd.getName(), cmd.getSyntax()), false);
            ebb.addField("Aliases", String.join(", ", cmd.getAliases()), false);
            message.reply(ebb.build());
            return true;
        } else {
            message.getChannel().sendMessage(String.format("%s Command %s not found", message.getUser().getAsMention(), args[2])).queue();
            return false;
        }
    }

    @Override
    public boolean hasPermission(User user, Member member, MessageChannel channel) {
        return true;
    }
}
