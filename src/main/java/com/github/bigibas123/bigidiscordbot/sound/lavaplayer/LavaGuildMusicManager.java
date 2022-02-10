package com.github.bigibas123.bigidiscordbot.sound.lavaplayer;

import com.github.bigibas123.bigidiscordbot.sound.generic.GenericGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.sound.generic.PlayState;
import com.github.bigibas123.bigidiscordbot.sound.objects.PlayListInfo;
import com.github.bigibas123.bigidiscordbot.sound.objects.TrackInfo;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.AccessLevel;
import lombok.Getter;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE) public class LavaGuildMusicManager extends GenericGuildMusicManager<AudioTrack> {

	private static AudioPlayerManager manager;
	@Getter(value = AccessLevel.PROTECTED, lazy = true) private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName() + "-" + this.getGuild().getName());
	private final AudioPlayer player;
	private final AudioEventListener listener;

	public LavaGuildMusicManager(Guild guild) {
		super(guild);
		this.player = getManager().createPlayer();
		this.listener = new AEL(this);
		this.player.addListener(this.listener);
	}

	private static AudioPlayerManager getManager() {
		if (manager == null) {
			DefaultAudioPlayerManager apm = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(apm);
			manager = apm;
		}
		return manager;
	}

	@Override
	protected void stopPlaying() {
		this.player.stopTrack();
	}

	@Override
	protected void search(
			String search,
			Runnable onNothingFound,
			Consumer<TrackInfo<AudioTrack>> onTrackFound,
			Consumer<PlayListInfo<AudioTrack>> onPlayListFound,
			Consumer<PlayListInfo<AudioTrack>> onSearchResults,
			Consumer<Throwable> onException
	) {
		manager.loadItem(search, new FunctionalResultHandler(track -> onTrackFound.accept(convert(track)), playlist -> {
			PlayListInfo<AudioTrack> pl = convert(playlist);
			if (playlist.isSearchResult()) {
				onSearchResults.accept(pl);
			} else {
				onPlayListFound.accept(pl);
			}
		}, onNothingFound, onException::accept));
	}

	@Override
	protected AudioSendHandler getSendHandler() {
		return new AudioPlayerWrapper(this.getPlayer());
	}

	@Override
	protected void resumePlaying() {
		this.player.setPaused(false);
	}

	@Override
	protected void seekToEnd() {
		AudioTrack curT;
		if ((curT = this.getPlayer().getPlayingTrack()) != null) {
			if (!this.seek(curT.getDuration() - 1)) {
				getLogger().warn("Someone tried to skip something unseekable");
				//TODO other ways of skipping track
			}
		}
	}

	@Override
	protected void pausePlaying() {
		this.getPlayer().setPaused(true);
	}

	private TrackInfo<AudioTrack> convert(AudioTrack track) {
		return new TrackInfo<>(track.makeClone(), track.getInfo().title.equals("Unknown title") ? track.getIdentifier() : track.getInfo().title, track.getDuration(), 0);
	}

	private PlayListInfo<AudioTrack> convert(AudioPlaylist playlist) {
		return new PlayListInfo<>(playlist.getName(), playlist.getTracks().stream().map(this::convert).collect(Collectors.toList()));
	}

	@Override
	protected boolean getPlaying() {
		return (!this.player.isPaused()) && (this.player.getPlayingTrack() != null);
	}

	@Override
	public int getVolume() {
		return this.player.getVolume();
	}

	@Override
	public void setVolume(int volume) {
		this.player.setVolume(volume);
	}

	@Override
	public boolean seek(long location) {
		if (this.player.getPlayingTrack() != null && this.player.getPlayingTrack().isSeekable()) {
			this.player.getPlayingTrack().setPosition(location);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void bootStrap() {
		this.playNext();
	}

	private void playNext() {
		getLogger().debug("Starting playback of next track");
		TrackInfo<AudioTrack> track = this.pollNextTrack();
		if (track != null) {
			if (!this.player.startTrack(track.getTrack(), true)) {
				getLogger().error("Failed loading track: " + track + " in guild: " + this.getGuild().getName());
				this.getCurrentReplyContext().reply("Failed starting next track");
			} else {
				getLogger().trace("Track " + track + " loaded successfully");
			}
		}
	}

	private static class AEL extends AudioEventAdapter {

		private final LavaGuildMusicManager gmm;
		private final Logger logger;

		public AEL(LavaGuildMusicManager lavaGuildMusicManager) {
			super();
			this.gmm = lavaGuildMusicManager;
			this.logger = lavaGuildMusicManager.getLogger();
		}

		private Logger getLogger() {
			return logger;
		}

		@Override
		public void onPlayerPause(AudioPlayer player) {
			getLogger().trace("AEL.onPlayerPause");
			if (gmm.getState() != PlayState.PAUSED) {
				getLogger().error("Player paused but super class was not in paused state but in " + gmm.getState());
				gmm.pausePlaying();
			}
		}

		@Override
		public void onPlayerResume(AudioPlayer player) {
			getLogger().trace("AEL.onPlayerResume");
			if (gmm.getState() != PlayState.PLAYING) {
				getLogger().error("Player paused but super class was not in playing state but in " + gmm.getState());
			}
		}

		@Override
		public void onTrackStart(AudioPlayer player, AudioTrack track) {
			getLogger().trace("AEL.onTrackStart");
		}

		@Override
		public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
			getLogger().trace("AEL.onTrackEnd");
			gmm.playNext();
		}

		@Override
		public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
			getLogger().error("AEL.onTrackException: " + "player = " + player + ", track = " + track + ", exception = " + exception);
		}

		@Override
		public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
			getLogger().warn("AEL.onTrackStuck: " + "player = " + player + ", track = " + track + ", thresholdMs = " + thresholdMs);
		}

	}

}
