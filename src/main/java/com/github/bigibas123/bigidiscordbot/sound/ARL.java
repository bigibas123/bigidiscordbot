package com.github.bigibas123.bigidiscordbot.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;

class ARL implements AudioLoadResultHandler {
    private final GuildMusicManager gmm;
    private final TextChannel channel;
    private User author;

    public ARL(GuildMusicManager guildMusicManager, TextChannel channel, User author) {
        this.gmm = guildMusicManager;
        this.channel = channel;
        this.author = author;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        gmm.queue(track);
        channel.sendMessage(this.author.getAsMention() + " track " + track.getInfo().title + " queued").queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        channel.sendMessage(this.author.getAsMention() + " loaded playlist of " + playlist.getTracks().size() + " songs").queue();
        for (AudioTrack track : playlist.getTracks()) {
            gmm.queue(track);
        }
    }

    @Override
    public void noMatches() {
        channel.sendMessage(this.author.getAsMention() + " found nothing").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        channel.sendMessage(this.author.getAsMention() + " search failed:" + exception.getLocalizedMessage() + "\r\n" +
                String.join("\r\n", (String[]) Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).toArray())).queue();
    }

}
