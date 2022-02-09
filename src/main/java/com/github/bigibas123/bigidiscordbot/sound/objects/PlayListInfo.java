package com.github.bigibas123.bigidiscordbot.sound.objects;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class PlayListInfo<T> {
	private final String name;
	private final List<TrackInfo<T>> tracks;

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
		return this.getTracks().get(i);
	}

}
