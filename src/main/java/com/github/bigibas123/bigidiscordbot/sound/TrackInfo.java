package com.github.bigibas123.bigidiscordbot.sound;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackInfo {
    private final String title;
    private final long duration;
    private int number;

    public TrackInfo(String title, long duration) {
        this.number = 0;
        this.title = title;
        this.duration = duration;
    }
}
