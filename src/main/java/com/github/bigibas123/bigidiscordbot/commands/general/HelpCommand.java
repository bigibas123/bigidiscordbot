package com.github.bigibas123.bigidiscordbot.commands.general;

import com.github.bigibas123.bigidiscordbot.commands.CommandHandling;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.Color;

public class HelpCommand extends ICommand {

	public HelpCommand() {
		super("Help", "Displays this menu", "[command]", "h");
	}

	public static boolean sendCommandList(ReplyContext rc) {
		EmbedBuilder ebb = new EmbedBuilder();
		ebb.setTitle("Help");
		ebb.appendDescription("/help [command] for more info");
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
		rc.reply(ebb.build());
		return true;
	}

	public static boolean sendCommandDescription(ReplyContext message, String... args) {
		EmbedBuilder ebb = new EmbedBuilder();
		ICommand cmd = CommandHandling.getCommands().get(args[0].toLowerCase());
		if (cmd != null) {
			ebb.setTitle(cmd.getName());
			ebb.setColor(Color.GREEN);
			ebb.appendDescription(cmd.getDescription()).appendDescription("\r\n");
			ebb.addField("Usage", String.format("/%s %s", cmd.getName(), cmd.getSyntax()), false);
			ebb.addField("Aliases", String.join(", ", cmd.getAliases()), false);
			message.reply(ebb.build());
			return true;
		} else {
			message.reply(String.format("Command %s not found", args[0]));
			return false;
		}
	}

	@Override
	public boolean execute(ReplyContext replyContext, String... args) {

		if (args.length > 0) {
			return sendCommandDescription(replyContext, args);
		} else {
			return sendCommandList(replyContext);
		}
	}

	@Override
	public boolean hasPermission(User user, Member member, MessageChannelUnion channel) {
		return true;
	}

	@Override
	protected SlashCommandData _getSlashCommandData(SlashCommandData c) {
		return c.addOptions(new OptionData(OptionType.STRING, "command", "Command you want to print help for", false).addChoices(CommandHandling
																																		 .getHelpList()
																																		 .stream()
																																		 .map(ICommand::getName)
																																		 .map(k -> new Command.Choice(k, k))
																																		 .toList()));
	}
}
