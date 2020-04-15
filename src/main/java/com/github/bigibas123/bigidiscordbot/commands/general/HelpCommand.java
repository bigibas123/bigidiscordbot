package com.github.bigibas123.bigidiscordbot.commands.general;

import com.github.bigibas123.bigidiscordbot.commands.CommandHandling;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class HelpCommand extends ICommand {

    public HelpCommand() {
        super("Help", "Displays this menu", "", "h");
    }

    @Override
    public boolean execute(Message message, String... args) {

        if (args.length == 2) {
            return sendCommandList(message);
        } else if (args.length > 2) {
            return sendCommandDescription(message, args);
        }
        return false;
    }

    public static boolean sendCommandList(Message message) {
        EmbedBuilder ebb = new EmbedBuilder();
        ebb.setFooter("Requested by @" + message.getAuthor().getName(), message.getAuthor().getEffectiveAvatarUrl());
        ebb.setTitle("Help");
        ebb.setColor(Color.GREEN);
        StringBuilder names = new StringBuilder();
        StringBuilder descriptions = new StringBuilder();
        boolean first = true;
        for (ICommand command : CommandHandling.getHelpList()) {
            if (!command.hasPermission(message.getAuthor(), message.getChannel())) continue;
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
        message.getChannel().sendMessage(ebb.build()).queue();
        return true;
    }

    public static boolean sendCommandDescription(Message message, String... args) {
        EmbedBuilder ebb = new EmbedBuilder();
        ebb.setFooter("Requested by @" + message.getAuthor().getName(), message.getAuthor().getEffectiveAvatarUrl());
        ICommand cmd = CommandHandling.getCommandList().get(args[2].toLowerCase());
        if (cmd != null) {
            ebb.setTitle(cmd.getName());
            ebb.setColor(Color.GREEN);
            ebb.appendDescription(cmd.getDescription()).appendDescription("\r\n");
            ebb.addField("Usage", String.format("%s %s %s", message.getJDA().getSelfUser().getAsMention(), cmd.getName(), cmd.getSyntax()), false);
            ebb.addField("Aliases", String.join(", ", cmd.getAliases()), false);
            message.getChannel().sendMessage(ebb.build()).queue();
            return true;
        } else {
            message.getChannel().sendMessage(String.format("%s Command %s not found", message.getAuthor().getAsMention(), args[2])).queue();
            return false;
        }
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return true;
    }
}
