package com.github.bigibas123.bigidiscordbot.commands.general;

import com.github.bigibas123.bigidiscordbot.Reference;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

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
	public boolean hasPermission(User user, Member member, MessageChannelUnion channel) {
		return Reference.ownerID.equals(user.getId());
	}

	@Override
	protected SlashCommandData _getSlashCommandData(SlashCommandData c) {
		return null;
	}

}
