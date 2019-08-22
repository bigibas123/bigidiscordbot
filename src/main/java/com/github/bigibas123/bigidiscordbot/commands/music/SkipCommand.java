package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.sound.GuildMusicManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class SkipCommand extends ICommand {
    public SkipCommand() {
        super("skip", "skips currently playing song", "", "next");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (!Main.soundManager.guildMusicManagerExists(message.getGuild())) {
            return false;
        }
        GuildMusicManager gmm = Main.soundManager.getGuildMusicManager(message.getGuild());
        gmm.playNextTrack();
        return true;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return channel.getType().isGuild();
    }
}
