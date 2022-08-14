package com.github.bigibas123.bigidiscordbot.sound.generic;

import com.github.bigibas123.bigidiscordbot.sound.objects.PlayListInfo;
import com.github.bigibas123.bigidiscordbot.sound.objects.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.github.bigibas123.bigidiscordbot.util.Emoji.oneToTen;

public class GenericSearchResultHandler<T> extends ListenerAdapter {

	private static final int MAX_SONG_COUNT = 5;

	private final ReplyContext replyContext;
	private final PlayListInfo<T> search;
	private final JDA jda;
	private final MessageEmbed embed;
	private final BiConsumer<TrackInfo<T>, Member> selectionHandler;

	public GenericSearchResultHandler(ReplyContext replyContext, PlayListInfo<T> search, BiConsumer<TrackInfo<T>, Member> selectionHandler, JDA jda) {
		this.replyContext = replyContext;
		this.selectionHandler = selectionHandler;
		this.jda = jda;
		this.search = search.limit(MAX_SONG_COUNT);
		int i = 1;
		for (TrackInfo<T> t : search.getTracks()) {
			t.setNumber(i++);
		}
		this.embed = buildEmbed();
	}

	private void sendMessage() {
		MessageBuilder msg = new MessageBuilder(this.embed);
		msg.setEmbeds(this.embed);
		int bound = Math.min(MAX_SONG_COUNT, this.search.size());
		Button[] buttons = new Button[bound];
		for (int i = 1; i <= bound; i++) {
			buttons[i - 1] = Button.primary(String.valueOf(i), oneToTen.get(i).e());
		}
		this.replyContext.reply(embed);
		this.replyContext.reply(ActionRow.of(buttons));
		this.jda.addEventListener(this);
	}

	private int emojiToInt(EmojiUnion reactionEmote) {
		if (reactionEmote.getType() == net.dv8tion.jda.api.entities.emoji.Emoji.Type.UNICODE) return -1;
		for (Map.Entry<Integer, Emoji> entry : oneToTen.entrySet()) {
			if (reactionEmote.equals(entry.getValue().e())) {
				return entry.getKey();
			}
		}
		return -1;
	}

	@Override
	public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
		if (event.getUserIdLong() == event.getJDA().getSelfUser().getIdLong()) return;

		if (replyContext.isIn(event.getChannel())) {
			if (event.getMessageIdLong() == replyContext.getCurrentReply().getIdLong()) {
				if (Utils.isDJ(event.getUser(), event.getGuild())) {
					int selection = emojiToInt(event.getEmoji());
					if (selection != -1) {
						this.selectionHandler.accept(this.search.get(selection - 1), event.getMember());
					} else {
						this.replyContext.reply(Emoji.WARNING);
					}
				}
				//if i do this it works even when not caching the user
				event.getReaction().removeReaction(new UserImpl(event.getUserIdLong(), null)).queue();
			}

		}
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		if (replyContext.isIn(event.getChannel())) {
			if (Utils.isDJ(event.getUser(), event.getGuild())) {
				if (event.getMessageIdLong() == replyContext.getCurrentReply().getIdLong()) {
					replyContext.setInteractionHook(event.deferEdit());
					int selection = Integer.parseInt(event.getButton().getId());
					if (selection != -1) {
						this.selectionHandler.accept(this.search.get(selection - 1), event.getMember());
					} else {
						this.replyContext.reply(Emoji.WARNING.e());
					}
				}
			}
		}
	}

	private MessageEmbed buildEmbed() {
		EmbedBuilder ebb = new EmbedBuilder();
		ebb.setFooter("Requested by @" + replyContext.getUser().getName(), replyContext.getUser().getEffectiveAvatarUrl());
		ebb.setTitle("Search results");
		ebb.setColor(0xFF00FF);
		StringBuilder number = new StringBuilder();
		StringBuilder title = new StringBuilder();
		StringBuilder time = new StringBuilder();
		boolean first = true;
		for (TrackInfo<T> track : search.getTracks()) {
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
