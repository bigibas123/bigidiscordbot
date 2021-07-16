package com.github.bigibas123.bigidiscordbot;

import com.github.bigibas123.bigidiscordbot.commands.CommandHandling;
import com.github.bigibas123.bigidiscordbot.util.ReactionScheduler;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.time.LocalDateTime;

public class Listener extends ListenerAdapter {

	private final int shardID;
	private CommandHandling handling;

	public Listener(int i) {
		this.shardID = i;
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		super.onReady(event);
		int siID = event.getJDA().getShardInfo().getShardId();
		event.getJDA().openPrivateChannelById(Reference.ownerID).queue(c ->
				c.sendMessage("Started shard:" + shardID + "_" + siID + " at " + LocalDateTime.now()).queue()
		);
		String activityString = MessageFormat.format("@mention help\t| [{0}/{1}]",
				siID != shardID ? (siID + 1) + "_" + (shardID + 1) : (siID + 1),
				event.getJDA().getShardInfo().getShardTotal()
		);
		Activity act = Activity.of(Activity.ActivityType.DEFAULT, activityString);
		event.getJDA().getPresence().setPresence(
				OnlineStatus.ONLINE,
				act,
				false
		);
		if(this.shardID==0){
			CommandHandling.handleSlashRegistration(event);
		}
		this.handling = this.handling != null ? this.handling : new CommandHandling();

	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event) {
		super.onGuildReady(event);
		this.handling = this.handling != null ? this.handling : new CommandHandling();
		this.handling.registerSlashCommandPerms(event.getGuild());
	}

	@Override
	public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
		super.onPrivateMessageReceived(event);
		if (!event.getAuthor().isBot()) {
			if (Utils.mentionsMe(event.getMessage())) {
				handling.handleChatCommand(event.getMessage());
			} else {
				MessageAction messageAction = event.getChannel().sendMessage(event.getMessage().getContentRaw().replace("i", "o"));
				messageAction.queue();
			}
		}
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		super.onGuildMessageReceived(event);
		if (!event.getAuthor().isBot()) {
			if (Utils.mentionsMe(event.getMessage())) {
				handling.handleChatCommand(event.getMessage());
			}
		}
	}

	@Override
	public void onSlashCommand(@NotNull SlashCommandEvent event) {
		super.onSlashCommand(event);
		handling.handleSlashCommand(event);
	}

	@Override
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
		super.onMessageReactionAdd(event);
		if (event.getReaction().isSelf()) {
			ReactionScheduler.check(event.getReaction());
		}
	}

}
