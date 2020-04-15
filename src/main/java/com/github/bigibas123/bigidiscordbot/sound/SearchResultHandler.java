package com.github.bigibas123.bigidiscordbot.sound;

import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class SearchResultHandler<T> extends ListenerAdapter {
    @Getter
    private final List<TrackInfo<T>> assignments;

    private final TextChannel channel;
    private final User author;
    private JDA jda;
    private Emoji[] oneToTen = new Emoji[]{Emoji.ONE, Emoji.TWO, Emoji.THREE, Emoji.FOUR, Emoji.FIVE, Emoji.SIX, Emoji.SEVEN, Emoji.EIGHT, Emoji.NINE, Emoji.TEN};
    private final MessageEmbed embed;
    private Message message;

    public SearchResultHandler(TextChannel channel, User author, ArrayList<TrackInfo<T>> playlist, JDA jda) {
        this.channel = channel;
        this.author = author;
        this.jda = jda;
        this.assignments = playlist.subList(0, Math.min(playlist.size(), 10));
        this.embed = buildEmbed();

    }

    private MessageEmbed buildEmbed() {
        EmbedBuilder ebb = new EmbedBuilder();
        ebb.setFooter("Requested by @" + author.getName(), author.getEffectiveAvatarUrl());
        ebb.setTitle("Search results");
        ebb.setColor(Color.MAGENTA);
        StringBuilder number = new StringBuilder();
        StringBuilder title = new StringBuilder();
        StringBuilder time = new StringBuilder();
        boolean first = true;
        for (TrackInfo<T> track : assignments) {
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

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if ((!Utils.isSameThing(event.getUser(), jda.getSelfUser())) &&
                Utils.isDJ(event.getUser(), event.getGuild()) &&
                Utils.isSameThing(event.getChannel(), this.channel) &&
                event.getMessageId().equals(message.getId())) {

            String toCompare = event.getReactionEmote().getName();

            int nummer = -1;
            for (int i = 0; i <= oneToTen.length; i++) {
                if (oneToTen[i].s().equals(toCompare)) {
                    nummer = i + 1;
                    break;
                }
            }
            if (nummer != -1) {
                this.jda.removeEventListener(this);
                int finalNummer = nummer;
                TrackInfo<T> t = this.assignments.stream().filter(l -> l.getNumber() == finalNummer).findAny().get();
                String title = t.getTitle();
                if (this.selected(t)) {
                    channel.sendMessage(this.author.getAsMention() + " track " + title + " queued").queue();
                } else {
                    channel.sendMessage(this.author.getAsMention() + "track " + title + " not queued something went wrong").queue();
                }
            }
        }
    }

    protected abstract boolean selected(TrackInfo<T> track);

    public void go() {

        channel.sendMessage(embed).queue(result -> {
            this.message = result;
            this.jda.addEventListener(this);
            Stream.of(oneToTen).forEach(e -> this.message.addReaction(e.s()).queue());
        });

    }
}
