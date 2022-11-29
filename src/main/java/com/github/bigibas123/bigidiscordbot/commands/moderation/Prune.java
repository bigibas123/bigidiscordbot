package com.github.bigibas123.bigidiscordbot.commands.moderation;

import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

public class Prune extends ICommand {

	public Prune() {
		super("Prune", "Removes messages", "[amount def=5]", "Purge", "Delete");
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean execute(ReplyContext replyContext, String... args) {
		int amount;
		if (args.length < 1) {
			amount = 5;
		} else {
			try {
				amount = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				replyContext.reply(args[0], "is not a number");
				return false;
			}
		}
		var orig = replyContext.isRegularMessage() ? replyContext.getOriginal().getIdLong() : -1;
		if (orig == -1) {

			long latestID = replyContext.getChannel().getLatestMessageIdLong();

			try {
				if (replyContext.getChannel().retrieveMessageById(latestID).complete() != null) {
					orig = latestID;
				} else {
					replyContext.reply("No messages in channel yet can't purge anything");
					return false;
				}
			} catch (ErrorResponseException e) {
				if (e.getErrorResponse() == UNKNOWN_MESSAGE) {
					replyContext.reply("No messages in channel yet can't purge anything");
					return false;
				} else {
					throw e;
				}
			}

		}
		if (replyContext.getChannel().getType() == ChannelType.PRIVATE) {
			AtomicInteger sleepCounter = new AtomicInteger();
			if (amount <= 100) {
				List<Message> hist = replyContext.getChannel().getHistoryBefore(orig, amount).complete().getRetrievedHistory();
				hist.parallelStream().filter(msg -> Utils.isSameThing(msg.getAuthor(), replyContext.getJDA().getSelfUser())).forEach(hm -> hm
						.delete()
						.queueAfter(sleepCounter.getAndIncrement(), TimeUnit.SECONDS));
			} else {
				while (amount > 0) {
					List<Message> hist = replyContext.getChannel().getHistoryBefore(orig, Math.min(amount, 100)).complete().getRetrievedHistory();
					hist.parallelStream().filter(msg -> Utils.isSameThing(msg.getAuthor(), replyContext.getJDA().getSelfUser())).forEach(hm -> hm
							.delete()
							.queueAfter(sleepCounter.getAndIncrement(), TimeUnit.SECONDS));
					amount -= 100;
				}
			}
		} else {
			if (amount <= 100) {
				List<Message> hist = replyContext.getChannel().getHistoryBefore(orig, amount).complete().getRetrievedHistory();
				hist.parallelStream().forEach(hm -> hm.delete().complete());
			} else {
				while (amount > 0) {
					List<Message> hist = replyContext.getChannel().getHistoryBefore(orig, Math.min(amount, 100)).complete().getRetrievedHistory();
					hist.parallelStream().forEach(hm -> hm.delete().complete());
					amount -= 100;
				}
			}
		}
		return true;
	}

	@Override
	public boolean hasPermission(User user, Member member, MessageChannelUnion channel) {
		if (!channel.getType().isGuild()) {
			return true;
		} else if(channel.getType().isMessage()){
			if (member == null) {
				throw new IllegalArgumentException("User:" + user + " does not seem to be a member of:" + channel.getName());
			} else {
				return PermissionUtil.checkPermission((IPermissionContainer) channel, member, Permission.MESSAGE_MANAGE);
			}
		}
		return false;
	}

	@Override
	protected SlashCommandData _getSlashCommandData(SlashCommandData c) {
		return c.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)).addOption(OptionType.INTEGER, "amount", "Amount of messages to remove (default 5)");
	}

}

