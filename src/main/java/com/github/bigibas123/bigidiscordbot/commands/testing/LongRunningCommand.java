package com.github.bigibas123.bigidiscordbot.commands.testing;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.Reference;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class LongRunningCommand extends ICommand {
	public LongRunningCommand() {
		super("Long", "**TESTCOMMAND** takes 4s", "", "waitCommand", "sleepTest");
	}

	@Override
	public boolean execute(ReplyContext replyContext, String... args) {
		try {
			Main.log.info("Tick");
			Thread.sleep(4000);
			Main.log.info("Tock");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean hasPermission(User user, Member member, MessageChannelUnion channel) {
		return user.getId().equals(Reference.ownerID);
	}

	@Override
	protected SlashCommandData _getSlashCommandData(SlashCommandData c) {
		return null;
	}

}
