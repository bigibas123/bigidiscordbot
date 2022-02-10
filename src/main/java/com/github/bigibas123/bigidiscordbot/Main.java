package com.github.bigibas123.bigidiscordbot;


import com.github.bigibas123.bigidiscordbot.sound.SoundManager;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audio.factory.DefaultSendFactory;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.*;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.List;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class Main {

	public static Logger log;
	public static SoundManager soundManager;
	//@Getter private static JDA jda;

	public static void main(String[] args) throws LoginException {
		log = LoggerFactory.getLogger("BigiDiscordBot");
		if (!Reference.varsSet) {
			System.exit(1);
		}
		soundManager = new SoundManager();

		final Collection<GatewayIntent> enabledIntents = List.of(GUILD_VOICE_STATES, GUILD_MESSAGES, GUILD_MESSAGE_REACTIONS, DIRECT_MESSAGES, DIRECT_MESSAGE_REACTIONS);
		final Collection<GatewayIntent> disabledIntents = List.of(GUILD_MEMBERS, GUILD_BANS, GUILD_EMOJIS, GUILD_WEBHOOKS, GUILD_INVITES, GUILD_PRESENCES, GUILD_MESSAGE_TYPING,
																  DIRECT_MESSAGE_TYPING);

		final Collection<CacheFlag> enabledCaches = List.of(MEMBER_OVERRIDES, ROLE_TAGS, VOICE_STATE, MEMBER_OVERRIDES, ROLE_TAGS);
		final Collection<CacheFlag> disabledCaches = List.of(ACTIVITY, EMOTE, CLIENT_STATUS, ONLINE_STATUS);

		final SessionController sessionController = new ConcurrentSessionController();
		sessionController.setConcurrency(Runtime.getRuntime().availableProcessors());

		ShardManager b = DefaultShardManagerBuilder
				.create(Reference.token, enabledIntents)
				.enableIntents(enabledIntents)
				.disableIntents(disabledIntents)
				.addEventListenerProvider(Listener::new)
				.enableCache(enabledCaches)
				.disableCache(disabledCaches)
				.setActivity(null)
				.setAudioSendFactory(new DefaultSendFactory())
				.setAutoReconnect(true)
				.setBulkDeleteSplittingEnabled(false)
				.setChunkingFilter(ChunkingFilter.NONE)
				.setCompression(Compression.ZLIB)
				.setContextEnabled(true)
				.setEnableShutdownHook(true)
				.setLargeThreshold(50)
				.setMaxBufferSize(4096)
				.setMaxReconnectDelay(64)
				.setMemberCachePolicy(MemberCachePolicy.DEFAULT)
				.setRawEventsEnabled(false)
				.setRelativeRateLimit(true)
				.setRequestTimeoutRetry(true)
				.setGatewayEncoding(GatewayEncoding.ETF)
				.setSessionController(sessionController)
				.setEventManagerProvider(shardID -> new InterfacedEventManager())
				.setShardsTotal(-1)
				.setStatus(OnlineStatus.ONLINE)
				.setToken(Reference.token)
				.build();
	}
}
