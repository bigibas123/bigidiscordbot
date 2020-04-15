package com.github.bigibas123.bigidiscordbot.sound;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackInfo<T> {

    private final T track;
    private final String title;
    private final long duration;
    private int number;

    public TrackInfo(String title, long duration, T track) {
        this.number = 0;
        this.title = title;
        this.duration = duration;
        this.track = track;
    }
}
