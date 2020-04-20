package com.github.bigibas123.bigidiscordbot.sound.lavaplayer;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.sound.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import lombok.Setter;
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


public class LavaGuildMusicManager implements IGuildMusicManager<AudioTrack> {

    @Getter
    private final AudioPlayer player;

    @Getter
    private final BlockingQueue<AudioTrack> queue;

    private final AudioPlayerManager manager;

    private final Guild guild;
    private final ATL listener;

    @Getter
    private boolean playing;

    public LavaGuildMusicManager(Guild guild) {
        this.manager = getNewAPL();
        this.guild = guild;
        this.queue = new LinkedBlockingQueue<>();
        this.player = manager.createPlayer();
        this.listener = new ATL(this);
        this.player.addListener(this.listener);
        this.player.setVolume(50);
        this.playing = false;
    }

    private static AudioPlayerManager getNewAPL() {
        DefaultAudioPlayerManager apm = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(apm);
        return apm;
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
        manager.loadItem(trackName, new ARL(this, channel, user, channel.getJDA(), trackName));
    }

    @Override
    public void skip() {
        if (this.player.getPlayingTrack() != null && this.player.getPlayingTrack().isSeekable()) {
            this.seek(this.player.getPlayingTrack().getDuration() - 1);
        } else {
            if (this.player.getPlayingTrack() != null) {
                this.listener.setSkipped(true);
                this.tryNext();
            }
        }
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
    public TrackInfo<AudioTrack> getCurrentTrack() {
        AudioTrack track = this.getPlayer().getPlayingTrack();
        return new TrackInfo<>(getTrackTitle(track), track.getDuration(), track);
    }

    @Override
    public ArrayList<TrackInfo<AudioTrack>> getQueued() {
        ArrayList<TrackInfo<AudioTrack>> list = new ArrayList<>();
        for (AudioTrack track: this.getQueue()) {
            TrackInfo<AudioTrack> info = new TrackInfo<>(getTrackTitle(track), track.getDuration(), track);
            list.add(info);
        }
        return list;
    }

    public static String getTrackTitle(AudioTrack track) {
        if (track == null) {
            return "";
        }
        String title = track.getInfo().title;
        if (title.equals("Unknown title")) title = track.getIdentifier();
        return title;
    }

    @Override
    public void setVolume(int volume) {
        this.player.setVolume(volume);
    }

    @Override
    public boolean seek(long location) {
        if (!this.player.getPlayingTrack().isSeekable()) {
            return false;
        } else {
            this.player.getPlayingTrack().setPosition(location);
            return true;
        }
    }

    /**
     * utility method for less clutter
     *
     * @return {@link AudioManager}
     */
    private AudioManager AM() {
        return guild.getAudioManager();
    }

    public int queue(AudioTrack track) {
        Main.log.debug("Queued:" + track.getInfo().title);
        if (!player.startTrack(track, true)) {
            return queue.offer(track) ? 1 : 0;
        } else {
            return 1;
        }
    }

    public int queue(Collection<AudioTrack> playlist) {
        int songCount = 0;
        for (AudioTrack track: playlist) {
            if (this.queue.offer(track)) {
                songCount++;
            } else {
                return songCount;
            }
        }
        if (this.queue.size() > 0) {
            player.startTrack(queue.poll(), true);
        }
        return songCount;
    }

    public void playNextTrack() {
        tryNext();
    }

    private void tryNext() {
        if (queue.size() > 0) {
            this.player.startTrack(queue.poll(), false);
            setPlaying(true);
        } else {
            setPlaying(false);
        }
    }

    /**
     * makes the bot display if its playing or not in the voice menu
     *
     * @param playing if the player is currently playing
     */
    private void setPlaying(boolean playing) {
        Main.log.debug("Setting playing mode " + playing + " for guild: " + this.guild.getName());
        if (playing) {
            AM().setSpeakingMode(SpeakingMode.VOICE);
            AM().setSelfMuted(false);
        } else {
            AM().setSelfMuted(true);
        }
        this.playing = playing;
    }


    public static class ATL extends AudioEventAdapter {

        private final LavaGuildMusicManager gmm;
        @Setter
        @Getter
        private boolean skipped;

        public ATL(LavaGuildMusicManager lavaGuildMusicManager) {
            this.gmm = lavaGuildMusicManager;
        }

        @Override
        public void onPlayerPause(AudioPlayer player) {
            Main.log.trace("TrackPause:" + this.gmm.guild.getName());
            gmm.setPlaying(false);
        }

        @Override
        public void onPlayerResume(AudioPlayer player) {
            Main.log.trace("TrackResume:" + this.gmm.guild.getName());
            gmm.setPlaying(true);
        }

        @Override
        public void onTrackStart(AudioPlayer player, AudioTrack track) {
            Main.log.trace("TrackStart:" + this.gmm.guild.getName());
            gmm.setPlaying(true);
        }

        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            Main.log.trace("TrackEnd:" + this.gmm.guild.getName());
            if (!this.skipped) {
                Main.log.trace("Calling playNextTrack()");
                gmm.playNextTrack();
            } else {
                Main.log.trace("Skipping playNextTrack() call");
                this.skipped = false;
            }
        }

    }

}
