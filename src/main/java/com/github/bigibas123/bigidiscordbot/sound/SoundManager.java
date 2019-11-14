package com.github.bigibas123.bigidiscordbot.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;

public class SoundManager {

    private final HashMap<Long, GuildMusicManager> guildMusicManagers;

    private final AudioPlayerManager apm;

    public enum AudioSourceType {
        YOUTUBE(new YoutubeAudioSourceManager()),
        BANDCAMP(new BandcampAudioSourceManager()),
        BEAM(new BeamAudioSourceManager()),
        SOUNDCLOUD(new SoundCloudAudioSourceManager()),
        TWITCH(new TwitchStreamAudioSourceManager()),
        VIMEO(new VimeoAudioSourceManager()),
        HTTP(new HttpAudioSourceManager());

        @Getter
        private final AudioSourceManager manager;

        AudioSourceType(AudioSourceManager obj) {
            this.manager = obj;
        }
    }

    public SoundManager() {
        this.apm = new DefaultAudioPlayerManager();
        for (AudioSourceType ast : AudioSourceType.values()) {
            this.apm.registerSourceManager(ast.getManager());
        }
        AudioSourceManagers.registerRemoteSources(this.apm);
        this.guildMusicManagers = new HashMap<>();
    }

    public boolean guildMusicManagerExists(Guild guild) {
        return this.guildMusicManagers.containsKey(guild.getIdLong());
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        long id = guild.getIdLong();
        if (!this.guildMusicManagers.containsKey(id)) {
            this.guildMusicManagers.put(id, new GuildMusicManager(this.apm, guild));
        }
        return this.guildMusicManagers.get(id);
    }

    public void removeGuildMusicManager(GuildMusicManager guildMusicManager) {
        this.guildMusicManagers.remove(guildMusicManager.getGuild().getIdLong());
    }

}
