package com.github.bigibas123.bigidiscordbot.sound.objects;

import java.util.List;

public record PlayListInfo<T>(String name, List<TrackInfo<T>> tracks) {
	public int size() {
		return this.tracks.size();
	}

	/**
	 * Limits the playlist to the first {@code count} songs
	 *
	 * @param count the amount of songs to limit to
	 *
	 * @return a new playlist with only {@code count} songs
	 */
	public PlayListInfo<T> limit(int count) {
		return new PlayListInfo<>(name, tracks.subList(0, Math.min(count, tracks.size())));
	}

	public TrackInfo<T> get(int i) {
		return this.tracks().get(i);
	}

}
