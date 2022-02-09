package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;

public class PauseCommand extends MusicCommand {
	public PauseCommand() {
		super("pause", "pauses the currently playing song", "", "resume");
	}

	@Override
	public boolean execute(ReplyContext replyContext, String... args) {
		if (!this.guildManagerExists(replyContext)) {
			replyContext.reply("no song is currently playing");
			return false;
		} else {
			IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);

			boolean playing = gmm.isPlaying();
			if (playing) {
				gmm.pause();
				replyContext.reply(Emoji.PAUSE);
			} else {
				gmm.unpause();
				replyContext.reply(Emoji.PLAY);
			}
			return true;
		}

	}
}
