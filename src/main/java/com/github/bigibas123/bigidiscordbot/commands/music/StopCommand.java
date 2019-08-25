package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.sound.GuildMusicManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class StopCommand extends ICommand {
    public StopCommand() {
        super("stop", "stops the music currently playing", "");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (Main.soundManager.guildMusicManagerExists(message.getGuild())) {
            GuildMusicManager gmm = Main.soundManager.getGuildMusicManager(message.getGuild());
            gmm.stop();
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return channel.getType().isGuild();
    }
}
