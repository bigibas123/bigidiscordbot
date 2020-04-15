package com.github.bigibas123.bigidiscordbot.sound.lavaplayer;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.sound.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class LavaGuildMusicManager implements IGuildMusicManager {
    @Getter
    private final AudioPlayer player;

    @Getter
    private final BlockingQueue<AudioTrack> queue;

    private final AudioPlayerManager manager;

    private final Guild guild;

    private boolean ignoreNext;
    @Getter
    private boolean playing;

    public LavaGuildMusicManager(Guild guild) {
        this.manager = getNewAPL();
        this.guild = guild;
        this.queue = new LinkedBlockingQueue<>();
        this.player = manager.createPlayer();
        this.player.addListener(new ATL(this));
        this.player.setVolume(50);
        this.ignoreNext = false;
        this.playing = false;
    }

    private static AudioPlayerManager getNewAPL() {
        DefaultAudioPlayerManager apm = new DefaultAudioPlayerManager();
        for (LavaGuildMusicManager.AudioSourceType ast : LavaGuildMusicManager.AudioSourceType.values()) {
            apm.registerSourceManager(ast.getManager());
        }
        AudioSourceManagers.registerRemoteSources(apm);
        return apm;
    }

    public static String getTrackTitle(AudioTrack track) {
        String title = track.getInfo().title;
        if (title.equals("Unknown title")) title = track.getIdentifier();
        return title;
    }

    @Override
    public boolean connect(VoiceChannel channel) {
        if (AM().isConnected()) {
            return Utils.isSameThing(AM().getConnectedChannel(), channel);
        } else {
            AM().openAudioConnection(channel);
            AM().setSendingHandler(new AudioPlayerWrapper(this.player));
            return true;
        }
    }

    @Override
    public void queue(String trackName, TextChannel channel, User user) {
        manager.loadItem(trackName, new ARL(this, channel, user,channel.getJDA()));
    }

    public int queue(AudioTrack track) {
        Main.log.fine("Queued:" + track.getInfo().title);
        if (!player.startTrack(track, true)) {
            return queue.offer(track) ? 1 : 0;
        } else {
            return 1;
        }
    }

    public int queue(Collection<AudioTrack> playlist) {
        int songCount = 0;
        for (AudioTrack track : playlist) {
            if (this.queue.offer(track)) {
                songCount++;
            } else {
                return songCount;
            }
        }
        if (this.queue.remainingCapacity() > 0) {
            player.startTrack(queue.poll(), true);
        }
        return songCount;
    }

    @Override
    public ArrayList<TrackInfo<?>> getQueued() {
        ArrayList<TrackInfo<?>> list = new ArrayList<>();
        for (AudioTrack track : this.getQueue()) {
            TrackInfo<AudioTrack> info = new TrackInfo<>(getTrackTitle(track), track.getDuration(), track);
            list.add(info);
        }
        return list;
    }

    @Override
    public void setVolume(int volume) {
        this.player.setVolume(volume);
    }

    public void playNextTrack() {
        if (this.ignoreNext) {
            this.ignoreNext = false;
            return;
        }
        tryNext();
    }

    private void tryNext() {
        if (queue.remainingCapacity() > 0) {
            this.player.startTrack(queue.poll(), false);
        } else {
            setPlaying(false);
        }
    }

    @Override
    public void skip() {
        this.ignoreNext = true;
        tryNext();
    }

    @Override
    public void stop() {
        this.player.stopTrack();
        AM().closeAudioConnection();
        this.player.destroy();
        this.queue.clear();
    }

    @Override
    public void pause() {
        this.getPlayer().setPaused(true);
        this.setPlaying(false);
    }

    @Override
    public void unpause() {
        this.getPlayer().setPaused(false);
        this.setPlaying(true);
    }

    @Override
    public TrackInfo<?> getCurrentTrack() {
        AudioTrack track = this.getPlayer().getPlayingTrack();
        return new TrackInfo<>(getTrackTitle(track), track.getDuration(), track);
    }

    /**
     * utility method for less clutter
     *
     * @return {@link AudioManager}
     */
    private AudioManager AM() {
        return guild.getAudioManager();
    }


    /**
     * makes the bot display if its playing or not in the voice menu
     *
     * @param playing if the player is currently playing
     */
    private void setPlaying(boolean playing) {
        if (playing) {
            AM().setSpeakingMode(SpeakingMode.VOICE);
            AM().setSelfMuted(false);
        } else {
            AM().setSelfMuted(true);
        }
        this.playing = playing;
    }

    public enum AudioSourceType {
        YOUTUBE(new YoutubeAudioSourceManager(true)),
        BANDCAMP(new BandcampAudioSourceManager()),
        BEAM(new BeamAudioSourceManager()),
        SOUNDCLOUD(SoundCloudAudioSourceManager.createDefault()),
        TWITCH(new TwitchStreamAudioSourceManager()),
        VIMEO(new VimeoAudioSourceManager()),
        HTTP(new HttpAudioSourceManager());

        @Getter
        private final AudioSourceManager manager;

        AudioSourceType(AudioSourceManager obj) {
            this.manager = obj;
        }
    }

    public static class ATL extends AudioEventAdapter {
        private final LavaGuildMusicManager gmm;

        public ATL(LavaGuildMusicManager lavaGuildMusicManager) {
            this.gmm = lavaGuildMusicManager;
        }

        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            Main.log.fine("TrackEnd:" + this.gmm.guild.getName());
            gmm.playNextTrack();
        }

        @Override
        public void onPlayerPause(AudioPlayer player) {
            Main.log.fine("TrackPause:" + this.gmm.guild.getName());
            gmm.setPlaying(false);
        }

        @Override
        public void onPlayerResume(AudioPlayer player) {
            Main.log.fine("TrackResume:" + this.gmm.guild.getName());
            gmm.setPlaying(true);
        }

        @Override
        public void onTrackStart(AudioPlayer player, AudioTrack track) {
            Main.log.fine("TrackStart:" + this.gmm.guild.getName());
            gmm.setPlaying(true);
        }
    }

}
