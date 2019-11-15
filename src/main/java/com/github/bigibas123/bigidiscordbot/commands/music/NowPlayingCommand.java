package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;

public class NowPlayingCommand extends MusicCommand {
    public NowPlayingCommand() {
        super("nowplaying", "shows the currently playing song", "", "np");
    }

    @Override
    public boolean execute(Message message, String... args) {
        GuildMusicManager gmm;
        if (this.guildManagerExists(message) && (gmm = this.getGuildManager(message)).isPlaying()) {
            AudioTrack track = gmm.getPlayer().getPlayingTrack();
            String title = track.getInfo().title;
            if (title.equals("Unknown title")) title = track.getIdentifier();
            message.getChannel().sendMessage(message.getAuthor().getAsMention() + " Currently playing: " + title).queue();
            return true;
        } else {
            message.getChannel().sendMessage(message.getAuthor().getAsMention() + " no song is currently playing").queue();
            return false;
        }
    }
}
