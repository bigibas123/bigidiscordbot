package com.github.bigibas123.bigidiscordbot.sound.generic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString(includeFieldNames = false)
@AllArgsConstructor
public enum PlayState {
	PLAYING("Playing"), SKIPPING("Skipping"), PAUSED("Paused"), STOPPED("Stopped"),
	;
	@Accessors(fluent = true)
	@Getter
	private final String s;
}
