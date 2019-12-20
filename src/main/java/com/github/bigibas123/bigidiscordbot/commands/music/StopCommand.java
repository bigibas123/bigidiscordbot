package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import net.dv8tion.jda.api.entities.Message;

public class StopCommand extends MusicCommand {
    public StopCommand() {
        super("stop", "stops the music currently playing", "");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (this.guildManagerExists(message)) {
            IGuildMusicManager gmm = this.getGuildManager(message);
            gmm.stop();
            return true;
        }
        return false;
    }
}
