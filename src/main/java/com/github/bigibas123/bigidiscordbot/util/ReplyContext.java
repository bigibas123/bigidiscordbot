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
import net.dv8tion.jda.internal.requests.CallbackContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
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

	private static String unsafeJoin(Object... elements) {
		Objects.requireNonNull(elements);
		// Number of elements not likely worth Arrays.stream overhead.
		StringJoiner joiner = new StringJoiner(" ");
		for (Object cs: elements) {
			joiner.add(cs.toString());
		}
		return joiner.toString();
	}

	public void reply(MessageEmbed embed) {
		slashSplit(reg -> reg.getChannel().sendMessage(new MessageBuilder(user.getAsMention()).setEmbeds(embed).build()).queue(),
			(mb, e) -> {
				List<MessageEmbed> l;
				try {
					var f = mb.getClass().getDeclaredField("embeds");
					f.setAccessible(true);
					//noinspection unchecked
					l = (List<MessageEmbed>) f.get(mb);
				} catch (NoSuchFieldException | IllegalAccessException er) {
					Main.log.error("Can't access embeds field in message builder via reflection!", er);
					Main.log.error(Arrays.stream(mb.getClass().getDeclaredFields()).map(Field::getName).toList().toString());
					l = new ArrayList<>(1);
				}
				l.add(embed);
				mb.setEmbeds(l.toArray(MessageEmbed[]::new));
			});
	}

	private void slashSplit(Consumer<Message> reg, BiConsumer<MessageBuilder, SlashCommandEvent> sl) {
		if (isRegularMessage()) {
			reg.accept(this.original);
		} else if (isSlashCommand()) {
			if (hasRepliedToSlash()) {
				var mb = new MessageBuilder(currentReply);
				sl.accept(mb, this.sCmdEvent);
				this.slashCommandReplyHandler(this.interactionHook.editOriginal(mb.build()).complete());
			} else {
				var mb = new MessageBuilder();
				sl.accept(mb, this.sCmdEvent);
				this.slashCommandSuccessHandler(this.sCmdEvent.reply(mb.build()).setEphemeral(false).complete());
			}
		} else {
			printUnrecognizedSource();
		}
	}

	public boolean isRegularMessage() {
		return original != null;
	}

	public boolean isSlashCommand() {
		return sCmdEvent != null;
	}

	private boolean hasRepliedToSlash() {
		return interactionHook != null;
	}

	private void slashCommandReplyHandler(Message msg) {
		this.currentReply = msg;
	}

	private void slashCommandSuccessHandler(InteractionHook h) {
		this.interactionHook = h;
		new Thread(() -> {
			CallbackContext.getInstance().close();
			this.currentReply = h.retrieveOriginal().complete();
		}).start();
	}

	private void printUnrecognizedSource() {
		Main.log.error("Replycontext is neither a message or a slashcommand! {}", this);
	}

	public @Nonnull
	Guild getGuild() {
		if (isRegularMessage()) {
			return this.original.getGuild();
		} else if (isSlashCommand()) {
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

	public boolean isIn(@NonNull MessageChannel channel) {
		return this.getChannel().getIdLong() == channel.getIdLong();
	}

	public void reply(String message) {
		slashSplit(
			orig -> orig.getChannel().sendMessage(user.getAsMention() + " " + message).queue(),
			(mb, e) -> mb.append(mb.length() > 0 ? "\n" : "").append(message)
		);
	}

	public void reply(Emoji @NonNull ... emojis) {
		slashSplit(msg -> {
				for (Emoji e: emojis) {
					msg.addReaction(e.s()).queue();
				}
			},
			(mb, b) -> {
				for (Emoji e: emojis) {
					mb.append(e);
				}
			}
		);

	}

	public void reply(Object... messages) {
		this.reply(unsafeJoin(" ", messages));
	}

	public void reply(@NonNull Emoji e) {
		slashSplit(
			msg -> msg.addReaction(e.s()).queue(),
			(mb, sce) -> mb.append(e)
		);
	}

}
