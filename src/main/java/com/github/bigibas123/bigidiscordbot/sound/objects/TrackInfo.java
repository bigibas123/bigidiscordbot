package com.github.bigibas123.bigidiscordbot.sound.objects;

import com.github.bigibas123.bigidiscordbot.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor public class TrackInfo<T> {

	private final T track;
	private final String title;
	private final long duration;
	private int number;

	public String toString() {
		return "TrackInfo(title=" + this.getTitle() + ", duration=" + Utils.formatDuration(this.getDuration()) + ", number=" + this.getNumber() + ")";
	}

}
