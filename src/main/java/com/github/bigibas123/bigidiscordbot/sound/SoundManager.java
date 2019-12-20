package com.github.bigibas123.bigidiscordbot.sound;

import com.github.bigibas123.bigidiscordbot.sound.lavaplayer.LavaGuildMusicManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

public class SoundManager {

    private final HashMap<Long, IGuildMusicManager> guildMusicManagers;

    private IGuildMusicManager getNewMM(Guild guild) {
        return new LavaGuildMusicManager(guild);
    }

    public SoundManager() {
        this.guildMusicManagers = new HashMap<>();
    }

    public boolean guildMusicManagerExists(Guild guild) {
        return this.guildMusicManagers.containsKey(guild.getIdLong());
    }

    public IGuildMusicManager getGuildMusicManager(Guild guild) {
        long id = guild.getIdLong();
        if (!this.guildMusicManagers.containsKey(id)) {
            this.guildMusicManagers.put(id, this.getNewMM(guild));
        }
        return this.guildMusicManagers.get(id);
    }

    public void removeGuildMusicManager(Guild guild) {
        this.guildMusicManagers.remove(guild.getIdLong());
    }

}
