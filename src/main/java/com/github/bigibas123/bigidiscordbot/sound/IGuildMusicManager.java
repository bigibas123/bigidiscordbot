package com.github.bigibas123.bigidiscordbot.sound;

import com.github.bigibas123.bigidiscordbot.sound.objects.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

import java.util.ArrayList;

public interface IGuildMusicManager<T> {
	/**
	 * joins the specified voice channel
	 * returns false if it couldn't
	 *
	 * @param channel the {@link AudioChannel} to connect to
	 *
	 * @return true if it could connect to the specified channel or is already connected to it
	 */
	boolean connect(AudioChannelUnion channel);

	void queue(String search, ReplyContext context);

	void skip();

	void stop();

	void pause();

	void unpause();

	boolean isPlaying();

	TrackInfo<T> getCurrentTrack();

	ArrayList<TrackInfo<T>> getQueued();

	void swapQueued(int first, int second);

	int getQueueSize();

	TrackInfo<T> getQueuedTrack(int position);

	int getVolume();

	void setVolume(int volume);

	boolean seek(long location);

}
