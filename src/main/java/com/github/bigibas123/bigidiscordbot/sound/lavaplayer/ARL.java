package com.github.bigibas123.bigidiscordbot.sound.lavaplayer;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.sound.SearchResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class ARL implements AudioLoadResultHandler {
    private final LavaGuildMusicManager gmm;
    private final TextChannel channel;
    private final User author;
    private JDA jda;
    private final String search;

    public ARL(LavaGuildMusicManager gmm, TextChannel channel, User author, JDA jda, String search) {
        this.gmm = gmm;
        this.channel = channel;
        this.author = author;
        this.jda = jda;
        this.search = search;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        String title = LavaGuildMusicManager.getTrackTitle(track);
        if (gmm.queue(track) > 0) {
            channel.sendMessage(this.author.getAsMention() + " track " + title + " queued").queue();
        } else {
            channel.sendMessage(this.author.getAsMention() + "track " + title + " not queued something went wrong").queue();
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if(playlist.isSearchResult()){
            SearchResultHandler<?> searchResultHandler = new LavaSearchResultHandler(this, this.channel, this.author, playlist, gmm, this.jda);
            searchResultHandler.go();
        } else {
            int amount = gmm.queue(playlist.getTracks());
            if (amount >= playlist.getTracks().size()) {
                channel.sendMessage(this.author.getAsMention() + " loaded playlist " + playlist.getName() + " (" + playlist.getTracks().size() + " songs)").queue();
            } else {
                channel.sendMessage(this.author.getAsMention() + " failed loading full playlist " + playlist.getName() + " (" + amount + "/" + playlist.getTracks().size() + ")").queue();
            }
        }
    }

    @Override
    public void noMatches() {
        if (!this.search.startsWith("ytsearch:")) {
            channel.sendMessage(this.author.getAsMention() + "Searching youtube for: " + this.search).queue();
            gmm.queue("ytsearch:" + this.search, this.channel, this.author);
        } else {
            channel.sendMessage(this.author.getAsMention() + " found nothing").queue();
        }
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        channel.sendMessage(this.author.getAsMention() + " search failed:" + exception.getLocalizedMessage() + "\r\n" +
                String.join("\r\n", (String[]) Arrays.stream(exception.getStackTrace()).map(StackTraceElement::getClassName).toArray())).queue();
        Main.log.warn("exeption at: loadFailed(FriendlyException)", exception);
    }

}
