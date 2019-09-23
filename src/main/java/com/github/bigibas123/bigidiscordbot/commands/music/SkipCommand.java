package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.GuildMusicManager;
import net.dv8tion.jda.core.entities.Message;

public class SkipCommand extends MusicCommand {
    public SkipCommand() {
        super("skip", "skips currently playing song", "", "next");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (!this.guildManagerExists(message)) {
            return false;
        }
        GuildMusicManager gmm = this.getGuildManager(message);
        gmm.skip();
        return true;
    }
}
