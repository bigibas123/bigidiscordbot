package com.github.bigibas123.bigidiscordbot.sound.generic;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.sound.objects.PlayListInfo;
import com.github.bigibas123.bigidiscordbot.sound.objects.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Getter(AccessLevel.PROTECTED) public abstract class GenericGuildMusicManager<T> implements IGuildMusicManager<T> {

	private final Guild guild;
	private final AudioManager audioManager;

	private final Queue<TrackInfo<T>> queue;
	private PlayState state;

	@Getter(AccessLevel.PUBLIC) private TrackInfo<T> currentTrack;

	private ReplyContext currentReplyContext;

	public GenericGuildMusicManager(Guild guild) {
		this.queue = new ConcurrentLinkedQueue<>();
		this.guild = guild;
		this.audioManager = guild.getJDA().getAudioManagerCache().stream().filter(a -> a.getGuild().getIdLong() == this.guild.getIdLong()).findFirst().orElse(this.guild.getAudioManager());
		this.audioManager.setSpeakingMode(SpeakingMode.VOICE);
	}

	@Override
	public boolean connect(AudioChannel channel) {
		if (this.getAudioManager().isConnected()) {
			return Utils.isSameThing(this.getAudioManager().getConnectedChannel(), channel);
		} else {
			try {
				this.getAudioManager().openAudioConnection(channel);
				this.state = PlayState.STOPPED;
				this.getAudioManager().setSendingHandler(this.getSendHandler());
				this.setVolume(20);
				return true;
			} catch (InsufficientPermissionException | UnsupportedOperationException | IllegalArgumentException e) {
				Main.log.error("Could not connect to channel: " + channel.getName() + ", " + channel.getGuild(), e);
				return false;
			}
		}
	}

	@Override
	public void skip() {
		this.state = PlayState.SKIPPING;
		this.updateState();
	}

	@Override
	public void stop() {
		this.state = PlayState.STOPPED;
		this.updateState();
	}

	@Override
	public void pause() {
		if (this.state != PlayState.PAUSED) {
			this.state = PlayState.PAUSED;
			this.updateState();
		}
	}

	@Override
	public void unpause() throws IllegalStateException {
		if (this.state == PlayState.PAUSED) {
			this.state = PlayState.PLAYING;
			this.updateState();
		} else {
			throw new IllegalStateException("Player is in " + this.state + " but is should be in " + PlayState.PAUSED);
		}
	}

	@Override
	public boolean isPlaying() {
		return this.state == PlayState.PLAYING;
	}

	@Override
	public ArrayList<TrackInfo<T>> getQueued() {
		return new ArrayList<>(this.queue);
	}

	@Override
	public void swapQueued(int first, int second) {
		ArrayList<TrackInfo<T>> oldqueue = new ArrayList<>(this.queue);
		Collections.swap(oldqueue, first, second);
		this.queue.clear();
		this.queue.addAll(oldqueue);
	}

	@Override
	public int getQueueSize() {
		return this.queue.size();
	}

	@Override
	public TrackInfo<T> getQueuedTrack(int position) {
		if (position < this.getQueue().size()) {
			@SuppressWarnings("unchecked") TrackInfo<T>[] trackInfos = this.getQueue().toArray(new TrackInfo[0]);
			return trackInfos[position];
		} else {
			return null;
		}
	}

	@Override
	public void queue(String search, ReplyContext replyContext) {
		this.currentReplyContext = replyContext;
		this.search(search, () -> {
			if (!search.startsWith("ytsearch:")) {
				replyContext.reply("Searching youtube for: " + search);
				this.queue("ytsearch:" + search, replyContext);
			} else {
				replyContext.reply("Found nothing for: " + search);
			}
		}, singleTrack -> {
			this.queue.offer(singleTrack);
			replyContext.reply("track: " + singleTrack.getTitle() + " queued");
			onTrackAdded();
		}, playlist -> {
			AtomicInteger count = new AtomicInteger();
			playlist.getTracks().forEach(e -> {
				if (this.queue.offer(e)) {
					count.incrementAndGet();
				}
			});
			if (count.get() == playlist.size()) {
				replyContext.reply("queued " + playlist.size() + " tracks from " + playlist.getName());
			} else {
				replyContext.reply("queued (" + count.get() + "/" + playlist.size() + ") tracks from " + playlist.getName());
			}
			onTrackAdded();
		}, searchResult -> new GenericSearchResultHandler<>(replyContext, searchResult, (e, u) -> {
			this.queue.offer(e);
			replyContext.reply("queued: " + e.getTitle());
			onTrackAdded();
		}, replyContext.getJDA()).go(), ex -> {
			getLogger().warn("Execption while searching for song:", ex);
			replyContext.reply("Failure searching tracks:" + ex.getMessage());
		});
	}

	private void onTrackAdded() {
		if (this.state == PlayState.STOPPED) {
			this.state = PlayState.PLAYING;
			updateState();
		}
	}

	private void updateState() {
		getLogger().debug("GGMM state is:" + state);
		switch (state) {
			case PLAYING -> {
				this.getAudioManager().setSelfMuted(false);
				if (!this.getPlaying()) {
					this.resumePlaying();
					if (!this.getPlaying()) {
						this.bootStrap();
					}
				}
			}
			case SKIPPING -> {
				this.getAudioManager().setSelfMuted(false);
				this.seekToEnd();
				this.state = PlayState.PLAYING;
			}
			case PAUSED -> {
				this.getAudioManager().setSelfMuted(true);
				this.pausePlaying();
			}
			case STOPPED -> {
				this.getAudioManager().setSelfMuted(true);
				this.getAudioManager().closeAudioConnection();
				this.stopPlaying();
				this.queue.clear();
			}
		}
	}

	protected TrackInfo<T> pollNextTrack() {
		if ((this.currentTrack = getQueue().poll()) != null) {
			return this.currentTrack;
		} else {
			this.state = PlayState.STOPPED;
			updateState();
			return null;
		}
	}

	protected abstract void stopPlaying();

	protected abstract void search(
			String search,
			Runnable onNothingFound,
			Consumer<TrackInfo<T>> onTrackFound,
			Consumer<PlayListInfo<T>> onPlayListFound,
			Consumer<PlayListInfo<T>> onSearchResults,
			Consumer<Throwable> onException
	);

	protected abstract AudioSendHandler getSendHandler();

	protected abstract void resumePlaying();

	protected abstract void seekToEnd();

	protected abstract void pausePlaying();

	protected abstract Logger getLogger();

	protected abstract boolean getPlaying();

	protected abstract void bootStrap();


}
