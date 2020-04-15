package com.github.bigibas123.bigidiscordbot.sound.lavaplayer;

import com.github.bigibas123.bigidiscordbot.sound.SearchResultHandler;
import com.github.bigibas123.bigidiscordbot.sound.TrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class LavaSearchResultHandler extends SearchResultHandler<AudioTrack> {

    private final LavaGuildMusicManager gmm;

    public LavaSearchResultHandler(ARL arl, TextChannel channel, User author, AudioPlaylist playlist, LavaGuildMusicManager gmm, JDA jda) {
        super(channel, author, convertToTrackInfo(playlist), jda);
        this.gmm = gmm;
    }

    private static ArrayList<TrackInfo<AudioTrack>> convertToTrackInfo(AudioPlaylist playlist) {
        ArrayList<TrackInfo<AudioTrack>> tracks = new ArrayList<>();
        int i = 1;
        for (AudioTrack track : playlist.getTracks()) {
            tracks.add(new TrackInfo<>(track, LavaGuildMusicManager.getTrackTitle(track), track.getDuration(), i));
            i++;
        }
        return tracks;
    }

    @Override
    protected boolean selected(TrackInfo<AudioTrack> track) {
        return this.gmm.queue(track.getTrack()) > 0;
    }
}
