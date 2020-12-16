package com.github.bigibas123.bigidiscordbot;


import com.github.bigibas123.bigidiscordbot.sound.SoundManager;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.ACTIVITY;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.CLIENT_STATUS;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.EMOTE;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.MEMBER_OVERRIDES;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;

public class Main {

    public static Logger log;
    public static SoundManager soundManager;
    //@Getter private static JDA jda;

    public static void main(String[] args) throws LoginException {
        log = LoggerFactory.getLogger("BigiDiscordBot");
        if(!Reference.varsSet){
            System.exit(1);
        }
        soundManager = new SoundManager();
        ShardManager b = DefaultShardManagerBuilder.create(Reference.token, new ArrayList<>())
                .enableIntents(GUILD_VOICE_STATES, GUILD_MESSAGES, GUILD_MESSAGE_REACTIONS, DIRECT_MESSAGES, DIRECT_MESSAGE_REACTIONS)
                .disableIntents(GUILD_MEMBERS, GUILD_BANS, GUILD_EMOJIS, GUILD_INVITES, GUILD_PRESENCES, GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING)
                .addEventListeners(new Listener())
                .enableCache(MEMBER_OVERRIDES)
                .disableCache(ACTIVITY, EMOTE, CLIENT_STATUS)
                .setActivity(null)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .setAutoReconnect(true)
                .setBulkDeleteSplittingEnabled(true)
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
                .setStatus(OnlineStatus.ONLINE)
                .setToken(Reference.token)
                .build();
        /*
        jda = JDABuilder.create(Reference.token, new ArrayList<>())
                .enableIntents(GUILD_VOICE_STATES,GUILD_MESSAGES,GUILD_MESSAGE_REACTIONS,DIRECT_MESSAGES,DIRECT_MESSAGE_REACTIONS)
                .disableIntents(GUILD_MEMBERS,GUILD_BANS,GUILD_EMOJIS,GUILD_INVITES,GUILD_PRESENCES,GUILD_MESSAGE_TYPING,DIRECT_MESSAGE_TYPING)
                .addEventListeners(new Listener())
                .enableCache(EMOTE,MEMBER_OVERRIDES)
                .disableCache(ACTIVITY,VOICE_STATE,CLIENT_STATUS)
                .setActivity(null)
                .setAudioSendFactory(DefaultSendSystem::new)
                .setAutoReconnect(true)
                .setBulkDeleteSplittingEnabled(true)
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
                .setStatus(OnlineStatus.ONLINE)
                .setToken(Reference.token)
                .useSharding(0,1)
                .build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
}
