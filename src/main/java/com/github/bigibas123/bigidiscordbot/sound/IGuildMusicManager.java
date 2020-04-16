package com.github.bigibas123.bigidiscordbot.sound;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.ArrayList;

public interface IGuildMusicManager<T> {
    /**
     * joins the specified voice channel
     * returns false if it couldn't
     *
     * @param channel the {@link VoiceChannel} to connect to
     * @return true if it could connect to the specified channel or is already connected to it
     */
    boolean connect(VoiceChannel channel);

    void queue(String trackName, TextChannel channel, User user);

    void skip();

    void stop();

    void pause();

    void unpause();

    boolean isPlaying();

    TrackInfo<T> getCurrentTrack();

    ArrayList<TrackInfo<T>> getQueued();

    void setVolume(int volume);
}
