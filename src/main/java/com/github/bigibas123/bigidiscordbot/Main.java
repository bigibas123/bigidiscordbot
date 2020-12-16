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
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

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
                .disableIntents(GUILD_MEMBERS, GUILD_BANS, GUILD_EMOJIS,GUILD_WEBHOOKS, GUILD_INVITES, GUILD_PRESENCES, GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING)
                .addEventListeners(new Listener())
                .enableCache(MEMBER_OVERRIDES,ROLE_TAGS,VOICE_STATE)
                .disableCache(ACTIVITY, EMOTE, CLIENT_STATUS )
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
    }
}
