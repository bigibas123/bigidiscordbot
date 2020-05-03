package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;

public class SkipCommand extends MusicCommand {
    public SkipCommand() {
        super("skip", "skips currently playing song", "", "next");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (!this.guildManagerExists(replyContext)) {
            return false;
        }
        IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);
        gmm.skip();
        return true;
    }
}
