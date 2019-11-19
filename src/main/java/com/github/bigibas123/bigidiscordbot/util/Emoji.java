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
    PLAY("\u25B6"),
    ONE("\u0031\uFE0F\u20E3"),
    TWO("\u0032\uFE0F\u20E3"),
    THREE("\u0033\uFE0F\u20E3"),
    FOUR("\u0034\uFE0F\u20E3"),
    FIVE("\u0035\uFE0F\u20E3"),
    SIX("\u0036\uFE0F\u20E3"),
    SEVEN("\u0037\uFE0F\u20E3"),
    EIGHT("\u0038\uFE0F\u20E3"),
    NINE("\u0039\uFE0F\u20E3"),
    TEN("\uD83D\uDD1F"),
    ;

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
