package com.github.bigibas123.bigidiscordbot;

import com.github.bigibas123.bigidiscordbot.commands.CommandHandling;
import com.github.bigibas123.bigidiscordbot.util.ReactionScheduler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class Listener extends ListenerAdapter {

    private CommandHandling handling;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);
        event.getJDA().getUserById(Reference.ownerID).openPrivateChannel()
                .queue(c -> c.sendMessage("Started at " + LocalDateTime.now().toString()).queue());
        this.handling = new CommandHandling();
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        super.onPrivateMessageReceived(event);
        if (!event.getAuthor().isBot()) {
            if (event.getMessage().isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER)) {
                handling.handleCommand(event.getMessage());
            } else {
                MessageAction messageAction = event.getChannel().sendMessage("Ping!");
                messageAction.queue();
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        if (!event.getAuthor().isBot()) {
            if (event.getMessage().isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER)) {
                handling.handleCommand(event.getMessage());
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        if (event.getReaction().isSelf()) {
            ReactionScheduler.check(event.getReaction());
        }
    }
}
