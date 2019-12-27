package com.github.bigibas123.bigidiscordbot.commands.music;

import net.dv8tion.jda.api.entities.Message;

public class StopCommand extends MusicCommand {
    public StopCommand() {
        super("stop", "stops the music currently playing", "");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (this.guildManagerExists(message)) {
            this.stopGuildManager(message);
            return true;
        }
        return false;
    }
}
