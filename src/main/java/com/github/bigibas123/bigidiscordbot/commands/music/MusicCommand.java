package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.entities.*;

public abstract class MusicCommand extends ICommand {

    public MusicCommand(String name, String description, String syntax, String... aliases) {
        super(name, description, syntax, aliases);
    }

    protected IGuildMusicManager getGuildManager(Message message) {
        return Main.soundManager.getGuildMusicManager(message.getGuild());
    }

    protected boolean guildManagerExists(Message message) {
        return Main.soundManager.guildMusicManagerExists(message.getGuild());
    }

    protected void stopGuildManager(Message message) {
        Main.soundManager.removeGuildMusicManager(message.getGuild());
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        if (channel.getType().isGuild()) {
            if (channel instanceof TextChannel) {
                return Utils.isDJ(user,((TextChannel) channel).getGuild());
            } else {
                return false;
            }
        } else {
            return channel.getType() == ChannelType.GROUP || channel.getType() == ChannelType.PRIVATE;
        }
    }
}
