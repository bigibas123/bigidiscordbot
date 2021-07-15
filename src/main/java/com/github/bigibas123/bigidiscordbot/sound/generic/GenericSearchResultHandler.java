package com.github.bigibas123.bigidiscordbot.sound.generic;

import com.github.bigibas123.bigidiscordbot.sound.objects.PlayListInfo;
import com.github.bigibas123.bigidiscordbot.sound.objects.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.entities.UserImpl;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.github.bigibas123.bigidiscordbot.util.Emoji.oneToTen;

public class GenericSearchResultHandler<T> extends ListenerAdapter {

	private final ReplyContext replyContext;
	private final PlayListInfo<T> search;
	private final JDA jda;
	private final MessageEmbed embed;
	private final BiConsumer<TrackInfo<T>, Member> selectionHandler;
	private Message message;

	public GenericSearchResultHandler(ReplyContext replyContext, PlayListInfo<T> search, BiConsumer<TrackInfo<T>,Member> selectionHandler, JDA jda) {
		this.replyContext = replyContext;
		this.selectionHandler = selectionHandler;
		this.jda = jda;
		this.search = search.limit(10);
		int i = 1;
		for(TrackInfo<T> t: search.getTracks()){
			t.setNumber(i++);
		}
		this.embed = buildEmbed();
	}

	private void sendMessage() {
		this.replyContext.reply(this.embed);
		this.replyContext.getChannel().sendMessageEmbeds(this.embed).queue(s -> {
			this.message = s;
			this.jda.addEventListener(this);
			int bound = Math.min(10, this.search.size());
			for (int i = 1; i <= bound; i++) {
				this.message.addReaction(oneToTen.get(i).s()).queue();
			}
		});
	}

	private int emojiToInt(MessageReaction.ReactionEmote reactionEmote) {
		if (reactionEmote.isEmote()) return -1;
		for (Map.Entry<Integer, Emoji> entry: oneToTen.entrySet()) {
			if (reactionEmote.getName().equals(entry.getValue().s())) {
				return entry.getKey();
			}
		}
		return -1;
	}

	@Override
	public void onMessageEmbed(@Nonnull MessageEmbedEvent event) {
		if (event.getChannel().getIdLong() == this.replyContext.getChannel().getIdLong()) {
			if (event.getMessageIdLong() != this.message.getIdLong()) {
				this.message.clearReactions().queue(s -> {
				}, f -> {
					if (f instanceof InsufficientPermissionException) {
						this.message.getReactions().forEach(messageReaction -> messageReaction.removeReaction().queue());
					}
				});
				this.jda.removeEventListener(this);
			}
		}
	}

	@Override
	public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
		if (event.getUserIdLong() == event.getJDA().getSelfUser().getIdLong()) return;

		if (replyContext.isIn(event.getChannel())) {
			if (Utils.isDJ(event.getUser(), event.getGuild())) {
				if (event.getMessageIdLong() == this.message.getIdLong()) {
					int selection = emojiToInt(event.getReactionEmote());
					if (selection != -1) {
						this.selectionHandler.accept(this.search.get(selection-1),event.getMember());
					} else {
						this.message.addReaction(Emoji.WARNING.s()).queue();
					}
				}
			}
			//if i do this it works even when not caching the user
			if (event.getReactionEmote().isEmote()) {
				message.removeReaction(event.getReactionEmote().getEmote(), new UserImpl(event.getUserIdLong(), null)).queue();
			} else {
				message.removeReaction(event.getReactionEmote().getEmoji(), new UserImpl(event.getUserIdLong(), null)).queue();
			}
		}
	}

	private MessageEmbed buildEmbed() {
		EmbedBuilder ebb = new EmbedBuilder();
		ebb.setFooter("Requested by @" + replyContext.getUser().getName(), replyContext.getUser().getEffectiveAvatarUrl());
		ebb.setTitle("Search results");
		ebb.setColor(Color.MAGENTA);
		StringBuilder number = new StringBuilder();
		StringBuilder title = new StringBuilder();
		StringBuilder time = new StringBuilder();
		boolean first = true;
		for (TrackInfo<T> track: search.getTracks()) {
			if (first) {
				first = false;
			} else {
				number.append("\r\n");
				title.append("\r\n");
				time.append("\r\n");
			}
			String t = track.getTitle();
			number.append(track.getNumber());
			title.append(t, 0, Math.min(t.length(), 40));
			time.append(Utils.formatDuration(track.getDuration()));
		}

		ebb.addField("Number", number.toString(), true);
		ebb.addField("Title", title.toString(), true);
		ebb.addField("Time", time.toString(), true);
		return ebb.build();
	}

	public void go() {
		this.sendMessage();
	}

}
