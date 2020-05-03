package com.github.bigibas123.bigidiscordbot.util;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;
import java.util.StringJoiner;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReplyContext {

	@NonNull private final MessageChannel channel;
	@NonNull private final User user;
	private final Message original;

	public ReplyContext(Message original) {
		this(original.getChannel(),original.getAuthor(),original);
	}

	private static String unsafeJoin(Object... elements) {
		Objects.requireNonNull(elements);
		// Number of elements not likely worth Arrays.stream overhead.
		StringJoiner joiner = new StringJoiner(" ");
		for (Object cs: elements) {
			joiner.add(cs.toString());
		}
		return joiner.toString();
	}

	public @NonNull JDA getJDA() {
		return this.channel.getJDA();
	}

	public void reply(String message) {
		this.channel.sendMessage(user.getAsMention() + " " + message).queue();
	}

	public void reply(MessageEmbed embed) {
		this.channel.sendMessage(user.getAsMention()).queue(s -> s.editMessage(embed).queue());
	}

	public void reply(String... messages) {
		this.reply(String.join(" ", messages));
	}

	public void reply(Object... messages) {
		this.reply(unsafeJoin(" ", messages));
	}

	public void reply(Emoji @NonNull ... emojis) {
		for (Emoji e: emojis) {
			this.original.addReaction(e.s()).queue();
		}
	}

	public void reply(@NonNull Emoji e) {
		this.original.addReaction(e.s()).queue();
	}

	public boolean isIn(@NonNull MessageChannel channel) {
		return this.getChannel().getIdLong() == channel.getIdLong();
	}

	public @NonNull Guild getGuild() {
		return original.getGuild();
	}

}
