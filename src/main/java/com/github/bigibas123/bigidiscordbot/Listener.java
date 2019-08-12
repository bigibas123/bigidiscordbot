package com.github.bigibas123.bigidiscordbot;

import com.github.bigibas123.bigidiscordbot.commands.CommandHandling;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

import java.time.LocalDateTime;

public class Listener extends ListenerAdapter {

    private CommandHandling handling;

    @Override
    public void onReady(ReadyEvent event) {
        super.onReady(event);
        event.getJDA().getUserById(166980806877642752L).openPrivateChannel()
                .queue(c -> c.sendMessage("Started at " + LocalDateTime.now().toString()).queue());
        this.handling = new CommandHandling();
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        super.onPrivateMessageReceived(event);
        if(!event.getAuthor().isBot()) {
            if (event.getMessage().isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER)) {
                handling.handleCommand(event.getMessage());
            } else {
                MessageAction messageAction = event.getChannel().sendMessage("Ping!");
                messageAction.queue();
            }
        }
    }
}
