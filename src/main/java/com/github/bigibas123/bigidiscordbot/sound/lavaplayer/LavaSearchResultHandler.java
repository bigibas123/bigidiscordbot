package com.github.bigibas123.bigidiscordbot.sound.lavaplayer;

import com.github.bigibas123.bigidiscordbot.sound.SearchResultHandler;
import com.github.bigibas123.bigidiscordbot.sound.TrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;

public class LavaSearchResultHandler extends SearchResultHandler {

    private final HashMap<Integer, AudioTrack> mappings;
    private final LavaGuildMusicManager gmm;

    public LavaSearchResultHandler(ARL arl, TextChannel channel, User author, AudioPlaylist playlist, LavaGuildMusicManager gmm, JDA jda) {
        super(arl, channel, author, convertToTrackInfo(playlist), gmm, jda);
        this.gmm = gmm;
        this.mappings = new HashMap<>();
        setMappings(playlist);
    }

    private static ArrayList<TrackInfo> convertToTrackInfo(AudioPlaylist playlist) {
        ArrayList<TrackInfo> tracks = new ArrayList<>();
        int i = 1;
        for (AudioTrack track : playlist.getTracks()) {
            tracks.add(new TrackInfo(LavaGuildMusicManager.getTrackTitle(track), track.getDuration(), i));
            i++;
        }
        return tracks;
    }

    private void setMappings(AudioPlaylist playlist) {
        int i = 1;
        for (AudioTrack track : playlist.getTracks()) {
            mappings.put(i, track);
            i++;
        }
    }

    @Override
    protected boolean selected(int nummer) {
        return this.gmm.queue(mappings.get(nummer)) > 0;
    }
}
