package com.github.bigibas123.bigidiscordbot.commands.testing;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.Reference;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.internal.requests.CallbackContext;

import java.util.Collection;
import java.util.List;

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
	public boolean hasPermission(User user, Member member, MessageChannel channel) {
		return user.getId().equals(Reference.ownerID);
	}

	@Override
	protected SlashCommandData _getSlashCommandData(SlashCommandData c) {
		return null;
	}

	@Override
	protected Collection<CommandPrivilege> _getPrivilegesForGuild(Guild g, List<Role> roles, List<CommandPrivilege> list) {
		CallbackContext.getInstance().close();
		Member owner = g.retrieveMemberById(Reference.ownerID).complete();
		if (owner != null) {
			list.add(CommandPrivilege.enable(owner.getUser()));
		}
		else {
			Main.log.info("Could not find info for: " + Reference.ownerID);
		}
		return list;
	}

}
