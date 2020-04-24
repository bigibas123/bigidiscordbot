package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import net.dv8tion.jda.api.entities.Message;

public class SkipCommand extends MusicCommand {
    public SkipCommand() {
        super("skip", "skips currently playing song", "", "next");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (!this.guildManagerExists(message)) {
            return false;
        }
        IGuildMusicManager<?> gmm = this.getGuildManager(message);
        gmm.skip();
        return true;
    }
}
