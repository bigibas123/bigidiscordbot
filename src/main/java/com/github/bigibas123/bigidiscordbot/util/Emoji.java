package com.github.bigibas123.bigidiscordbot.util;

public enum Emoji {
    STOP_SIGN("\uD83D\uDED1"),
    RUNNER("\uD83C\uDFC3"),
    CHECK_MARK("\u2705"),
    CROSS("❌"),
    STOP_WATCH("\u23F1"),
    SHRUG("\uD83E\uDD37"),
    WAVE("\uD83D\uDC4B"),
    PAUSE("\u23F8"),
    PLAY("\u23E9");

    private final String toString;

    Emoji(String s) {
        this.toString = s;
    }

    public String s() {
        return this.toString;
    }

    @Override
    public String toString() {
        return this.toString;
    }
}
