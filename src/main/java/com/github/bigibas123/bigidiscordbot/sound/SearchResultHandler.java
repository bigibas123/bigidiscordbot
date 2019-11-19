package com.github.bigibas123.bigidiscordbot.sound;

import com.github.bigibas123.bigidiscordbot.util.Utils;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class SearchResultHandler extends ListenerAdapter {
    private Emoji[] oneToTen = new Emoji[]{Emoji.ONE, Emoji.TWO, Emoji.THREE, Emoji.FOUR, Emoji.FIVE, Emoji.SIX, Emoji.SEVEN, Emoji.EIGHT, Emoji.NINE, Emoji.TEN};

    private final ARL arl;
    private final TextChannel channel;
    private final User author;
    private GuildMusicManager gmm;
    private JDA jda;
    private final List<AudioTrack> songs;
    private final HashMap<Integer, AudioTrack> assignments;
    private final MessageEmbed embed;
    private Message message;

    public SearchResultHandler(ARL arl, TextChannel channel, User author, AudioPlaylist playlist, GuildMusicManager gmm, JDA jda) {
        this.arl = arl;
        this.channel = channel;
        this.author = author;
        this.gmm = gmm;
        this.jda = jda;
        int searchResultSize = playlist.getTracks().size();
        this.songs = playlist.getTracks().subList(0, searchResultSize > 10 ? 10 : searchResultSize);
        this.assignments = new HashMap<>();
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
        int i = 1;
        boolean first = true;
        for (AudioTrack track : this.songs) {
            if (first) {
                first = false;
            } else {
                number.append("\r\n");
                title.append("\r\n");
                time.append("\r\n");
            }
            String t = Utils.getTrackTitle(track);
            number.append(i);
            title.append(t, 0, t.length() <= 40 ? t.length() : 40);
            time.append(Utils.formatDuration(track.getDuration()));
            this.assignments.put(i, track);
            i++;
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
                    nummer = i;
                    break;
                }
            }
            if (nummer != -1) {
                this.jda.removeEventListener(this);
                AudioTrack track = this.assignments.get(nummer);
                String title = Utils.getTrackTitle(track);
                if (gmm.queue(track) > 0) {
                    channel.sendMessage(this.author.getAsMention() + " track " + title + " queued").queue();
                } else {
                    channel.sendMessage(this.author.getAsMention() + "track " + title + " not queued something went wrong").queue();
                }
            }
        }
    }

    public void go() {

        channel.sendMessage(embed).queue(result -> {
            this.message = result;
            this.jda.addEventListener(this);
            Stream.of(oneToTen).forEach(e -> this.message.addReaction(e.s()).queue());

        });

    }
}
