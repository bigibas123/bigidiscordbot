package com.github.bigibas123.bigidiscordbot.util;

import com.github.bigibas123.bigidiscordbot.Main;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReplyContext {

	@NonNull
	private final MessageChannel channel;
	@NonNull
	private final User user;
	@Nullable
	private final Member member;
	@Nullable
	private final Message original;
	@Nullable
	private final SlashCommandEvent sCmdEvent;
	@Nullable
	private InteractionHook interactionHook;
	@Nullable
	private Message currentReply;

	public ReplyContext(Message original) {
		this(original.getChannel(), original.getAuthor(), original.getMember(), original, null);
	}

	public ReplyContext(SlashCommandEvent sCmdEvent) {
		this(sCmdEvent.getChannel(), sCmdEvent.getUser(), sCmdEvent.getMember(), null, sCmdEvent);
	}

	public @NonNull JDA getJDA() {
		return this.channel.getJDA();
	}

	public void reply(MessageEmbed embed) {
		slashSplit((mb, msg) -> {
			List<MessageEmbed> l = new LinkedList<>(msg.getEmbeds());
			l.add(embed);
			mb.setEmbeds(l.toArray(MessageEmbed[]::new));
		});
	}

	private void slashSplit(BiConsumer<MessageBuilder, Message> sl) {
		if (isRegularMessage()) {
			MessageBuilder mb;
			if (currentReply == null) {
				mb = new MessageBuilder();
				sl.accept(mb, null);
				this.currentReply = this.original.reply(mb.build()).complete();
			} else {
				mb = new MessageBuilder(currentReply);
				sl.accept(mb, this.currentReply);
				this.currentReply = this.currentReply.editMessage(mb.build()).complete();
			}
		} else if (sCmdEvent != null) {
			if (interactionHook != null) {
				var mb = new MessageBuilder(currentReply);
				sl.accept(mb, this.currentReply);
				this.currentReply = this.interactionHook.editOriginal(mb.build()).complete();
			} else {
				var mb = new MessageBuilder();
				sl.accept(mb, this.currentReply);
				this.interactionHook = this.sCmdEvent.reply(mb.build()).setEphemeral(false).complete();
				this.currentReply = this.interactionHook.retrieveOriginal().complete();
			}
		} else {
			printUnrecognizedSource();
		}
	}

	public boolean isRegularMessage() {
		return original != null;
	}

	private void printUnrecognizedSource() {
		Main.log.error("Replycontext is neither a message or a slashcommand! {}", this);
	}

	public void reply(Emoji @NonNull ... emojis) {
		slashSplit((mb, b) -> {
			for (Emoji e : emojis) {
				if (this.currentReply != null) {
					this.currentReply.addReaction(e.s()).queue();
				} else {
					mb.append(e);
				}
			}
		});

	}

	public @Nonnull
	Guild getGuild() {
		if (isRegularMessage()) {
			return this.original.getGuild();
		} else if (sCmdEvent != null) {
			return Objects.requireNonNull(this.sCmdEvent.getGuild());
		} else {
			printUnrecognizedSource();
			//noinspection ConstantConditions
			return null;
		}
	}

	public void reply(String... messages) {
		this.reply(String.join(" ", messages));
	}

	public void reply(String message) {
		slashSplit((mb, msg) -> mb.append(mb.length() > 0 ? "\n" : "").append(message));
	}

	public void reply(ActionRow... rows) {
		slashSplit((mb, msg) -> {
			List<ActionRow> l = new LinkedList<>(msg.getActionRows());
			l.addAll(Arrays.asList(rows));
			mb.setActionRows(l);
		});
	}

	public void reply(Object... messages) {
		this.reply(unsafeJoin(messages));
	}

	private static String unsafeJoin(Object... elements) {
		Objects.requireNonNull(elements);
		// Number of elements not likely worth Arrays.stream overhead.
		StringJoiner joiner = new StringJoiner(" ");
		for (Object cs : elements) {
			joiner.add(cs.toString());
		}
		return joiner.toString();
	}

	public boolean isIn(@NonNull MessageChannel channel) {
		return this.getChannel().getIdLong() == channel.getIdLong();
	}

	public void reply(@NonNull Emoji e) {
		slashSplit((mb, msg) -> {
			if (this.currentReply != null) {
				this.currentReply.addReaction(e.s()).queue();
			} else {
				mb.append(e);
			}
		});
	}

	public String getOriginalText() {
		CompletableFuture<String> fut = new CompletableFuture<>();
		slashSplit(
				(mb, sce) -> fut.complete(this.sCmdEvent.getName() + " " + this.sCmdEvent.getOptions().stream().map(OptionMapping::getAsString).collect(Collectors.joining(" ")))
		);
		try {
			return fut.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

}
