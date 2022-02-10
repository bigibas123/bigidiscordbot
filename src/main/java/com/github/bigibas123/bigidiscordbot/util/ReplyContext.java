package com.github.bigibas123.bigidiscordbot.util;

import com.github.bigibas123.bigidiscordbot.Main;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Data @RequiredArgsConstructor(access = AccessLevel.PRIVATE) public final class ReplyContext {

	@Nonnull private final MessageChannel channel;
	@Nonnull private final User user;
	@Nullable private final Member member;
	@Nullable private final Message original;
	@Nullable private final SlashCommandInteractionEvent sCmdEvent;
	@NonNull private final Semaphore hookLock = new Semaphore(1);
	@Nullable private InteractionHook interactionHook;
	@Nullable private Message currentReply;

	public ReplyContext(Message original) {
		this(original.getChannel(), original.getAuthor(), original.getMember(), original, null);
	}

	public ReplyContext(SlashCommandInteractionEvent sCmdEvent) {
		this(sCmdEvent.getChannel(), sCmdEvent.getUser(), sCmdEvent.getMember(), null, sCmdEvent);
		this.hookLock.lock();
		this.sCmdEvent.deferReply().queue(ih -> {
			interactionHook = ih;
			hookLock.unlock();
		}, throwable -> {
			hookLock.unlock();
			Main.log.warn("Exception on first setting interaction hook", throwable);
		});
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

	public <T extends RestAction<InteractionHook>> void setInteractionHook(@Nullable T rcA) {
		hookLock.lock();
		if (rcA != null) {
			rcA.queue(interactionHook1 -> {
				this.interactionHook = interactionHook1;
				hookLock.unlock();
			}, throwable -> {
				hookLock.unlock();
				Main.log.warn("Exception in setting interaction hook", throwable);
			});
		} else {
			this.interactionHook = null;
			hookLock.unlock();
		}
	}

	public @Nonnull
	JDA getJDA() {
		return this.channel.getJDA();
	}

	public void reply(MessageEmbed embed) {
		slashSplit((mb, msg) -> {
			List<MessageEmbed> l = msg != null ? (new LinkedList<>(msg.getEmbeds())) : new LinkedList<>();
			l.add(embed);
			mb.setEmbeds(l.toArray(MessageEmbed[]::new));
		});
	}

	private void slashSplit(BiConsumer<MessageBuilder, Message> sl) {
		try (var ignored = hookLock.lock()) {
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
					Main.log.warn("No interaction hook present!", new EmptyStackException());
					var mb = new MessageBuilder();
					sl.accept(mb, this.currentReply);
					this.interactionHook = this.sCmdEvent.reply(mb.build()).setEphemeral(false).complete();
					this.currentReply = this.interactionHook.retrieveOriginal().complete();
				}
			} else {
				printUnrecognizedSource();
			}
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
		slashSplit((mb, sce) -> {
			if (isRegularMessage()) {
				fut.complete(this.getOriginal().getContentRaw());
			} else {
				fut.complete(this.sCmdEvent.getName() + " " + this.sCmdEvent.getOptions().stream().map(OptionMapping::getAsString).collect(Collectors.joining(" ")));
			}
		});
		try {
			return fut.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

}
