package com.github.bigibas123.bigidiscordbot.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class HelpCommand extends ICommand {
    private ArrayList<ICommand> helpList;

    public HelpCommand(ArrayList<ICommand> helpList) {
        super("Help", "Displays this menu", "", "h");
        this.helpList = helpList;
    }

    @Override
    public boolean execute(Message message, String... args) {
        EmbedBuilder ebb = new EmbedBuilder();
        ebb.setFooter("Requested by @" + message.getAuthor().getName(), message.getAuthor().getEffectiveAvatarUrl());
        if (args.length == 2) {
            ebb.setTitle("Help");
            ebb.setColor(Color.GREEN);
            StringBuilder names = new StringBuilder();
            StringBuilder descriptions = new StringBuilder();
            boolean first = true;
            for (ICommand command : helpList) {
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
        } else if (args.length > 2) {
            Optional<ICommand> oCmd = helpList.stream().filter(c -> args[2].equalsIgnoreCase(c.getName())).findFirst();
            if (oCmd.isPresent()) {
                ICommand cmd = oCmd.get();
                ebb.setTitle(cmd.getName());
                ebb.setColor(Color.GREEN);
                ebb.appendDescription(cmd.getDescription()).appendDescription("\r\n");
                ebb.addField("Usage", String.format("%s %s %s", message.getJDA().getSelfUser().getAsMention(), cmd.getName(), cmd.getSyntax()), false);
                ebb.addField("Aliases", String.join(", ", cmd.getAliases()), false);
                message.getChannel().sendMessage(ebb.build()).queue();
            } else {
                message.getChannel().sendMessage(String.format("%s Command %snot found", message.getAuthor().getAsMention(), args[2])).queue();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return true;
    }
}
