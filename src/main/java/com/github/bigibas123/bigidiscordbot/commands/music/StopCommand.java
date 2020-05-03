package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.util.ReplyContext;

public class StopCommand extends MusicCommand {
    public StopCommand() {
        super("stop", "stops the music currently playing", "");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (this.guildManagerExists(replyContext)) {
            this.stopGuildManager(replyContext);
            return true;
        }
        return false;
    }
}
