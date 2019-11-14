package com.github.bigibas123.bigidiscordbot.sound;

import com.github.bigibas123.bigidiscordbot.Main;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import net.dv8tion.jda.core.audio.SpeakingMode;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Getter
public class GuildMusicManager {

    private final AudioPlayer player;

    private final BlockingQueue<AudioTrack> queue;

    private final AudioPlayerManager manager;

    @Getter
    private final Guild guild;

    private final ATL atl;

    private boolean ignoreNext;
    @Getter
    private boolean playing;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        this.manager = manager;
        this.guild = guild;
        this.queue = new LinkedBlockingQueue<>();
        this.atl = new ATL(this);
        this.player = manager.createPlayer();
        this.player.addListener(this.atl);
        this.player.setVolume(50);
        this.ignoreNext = false;
        this.playing = false;
    }

    /**
     * joins the specified voice channel
     * returns false if it couldn't
     *
     * @param channel the {@link VoiceChannel} to connect to
     * @return if it could connect to the specified channel or are already connected to it
     */
    public boolean connect(VoiceChannel channel) {
        if (AM().isConnected()) {
            return AM().getConnectedChannel().getId().equals(channel.getId());
        } else {
            AM().openAudioConnection(channel);
            AM().setSendingHandler(new AudioPlayerWrapper(this.player));
            return true;
        }
    }

    public void queue(String trackName, TextChannel channel, User user) {
        manager.loadItem(trackName, new ARL(this, channel, user));
    }

    /**
     * Queue's and audio track
     * If nothing is currently playing starts playback as well
     *
     * @param track the audio track to queue
     * @return amount of tracks queued or started playing
     */
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

    /**
     * @return the currently queued songs
     * <p>
     * The returned array will be "safe" in that no references to it are
     * maintained by this queue.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     */
    public AudioTrack[] getQueued() {
        return this.getQueue().toArray(new AudioTrack[0]);
    }

    /**
     * starts the next track or stops playback if queue is done
     */
    public void playNextTrack() {
        if(this.ignoreNext){
            this.ignoreNext = false;
            return;
        }
        if (queue.remainingCapacity() > 0) {
            this.player.startTrack(queue.poll(), false);
        } else {
            setPlaying(false);
            this.stop();
        }
    }

    public void skip(){
        this.ignoreNext = true;
        if (queue.remainingCapacity() > 0) {
            this.player.startTrack(queue.poll(), false);
        } else {
            setPlaying(false);
            this.stop();
        }
    }

    public void stop() {
        this.player.stopTrack();
        AM().closeAudioConnection();
        this.player.destroy();
        this.queue.clear();
        Main.soundManager.removeGuildMusicManager(this);
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

    private static class ATL extends AudioEventAdapter {
        private final GuildMusicManager gmm;

        public ATL(GuildMusicManager guildMusicManager) {
            this.gmm = guildMusicManager;
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
