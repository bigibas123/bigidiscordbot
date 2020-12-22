package com.github.bigibas123.bigidiscordbot;

import com.github.bigibas123.bigidiscordbot.commands.CommandHandling;
import com.github.bigibas123.bigidiscordbot.util.ReactionScheduler;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.*;
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
        event.getJDA().openPrivateChannelById(Reference.ownerID).queue(c ->
                c.sendMessage("Started at " + LocalDateTime.now().toString()).queue()
        );
        String activityString = MessageFormat.format("@mention help\t| [{0}/{1}_{2}]", event.getJDA().getShardInfo().getShardId() + 1, event.getJDA().getShardInfo().getShardTotal(),this.shardID);
        Activity act = Activity.of(Activity.ActivityType.DEFAULT, activityString);
        event.getJDA().getPresence().setPresence(
                OnlineStatus.ONLINE,
                act,
                false
        );
        this.handling = new CommandHandling();
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        super.onPrivateMessageReceived(event);
        if (!event.getAuthor().isBot()) {
            if (Utils.mentionsMe(event.getMessage())) {
                handling.handleCommand(event.getMessage());
            } else {
                MessageAction messageAction = event.getChannel().sendMessage(event.getMessage().getContentRaw().replace("i","o"));
                messageAction.queue();
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        if (!event.getAuthor().isBot()) {
            if (Utils.mentionsMe(event.getMessage())) {
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
