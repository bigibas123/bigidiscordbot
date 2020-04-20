package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class NowPlayingCommand extends MusicCommand {

    public NowPlayingCommand() {
        super("nowplaying", "shows the currently playing song", "", "np");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (this.guildManagerExists(message)) {
            IGuildMusicManager<?> gmm = this.getGuildManager(message);
            if (gmm.isPlaying()) {
                Main.log.trace("Guild Playing Status: " + gmm.isPlaying());
                String title = gmm.getCurrentTrack().getTitle();
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " Currently playing: " + title).queue();
                return true;
            }
        }
        message.getChannel().sendMessage(message.getAuthor().getAsMention() + " no song is currently playing").queue();
        return false;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return channel.getType().isGuild() || channel.getType() == ChannelType.GROUP;
    }

}
