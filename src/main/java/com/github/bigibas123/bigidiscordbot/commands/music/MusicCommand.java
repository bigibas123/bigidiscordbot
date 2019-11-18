package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.sound.GuildMusicManager;
import net.dv8tion.jda.api.entities.*;

public abstract class MusicCommand extends ICommand {

    public MusicCommand(String name, String description, String syntax, String... aliases) {
        super(name, description, syntax, aliases);
    }

    protected GuildMusicManager getGuildManager(Message message) {
        return Main.soundManager.getGuildMusicManager(message.getGuild());
    }

    protected boolean guildManagerExists(Message message) {
        return Main.soundManager.guildMusicManagerExists(message.getGuild());
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        if (channel.getType().isGuild()) {
            if (channel instanceof TextChannel) {
                TextChannel txtChannel = ((TextChannel) channel);
                Guild guild = txtChannel.getGuild();
                Member member = guild.getMember(user);
                if (guild.getRolesByName("DJ", true).size() > 0) {
                    return member.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("DJ"));
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return channel.getType() == ChannelType.GROUP;
        }
    }
}
