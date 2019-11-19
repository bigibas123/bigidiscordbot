package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.GuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class QueueCommand extends MusicCommand {
    public QueueCommand() {
        super("queue", "list all queued songs for this guild", "", "qeueu");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (this.guildManagerExists(message)) {
            GuildMusicManager gmm = this.getGuildManager(message);
            AudioTrack[] tracks = gmm.getQueued();
            EmbedBuilder ebb = new EmbedBuilder();
            ebb.setTitle(String.format("Queue for %s", message.getGuild().getName()));
            ebb.setFooter(String.format("Requested by @%s", message.getAuthor().getName()), message.getAuthor().getEffectiveAvatarUrl());
            int i = 1;
            int more = 0;

            for (AudioTrack track : tracks) {
                if (more == 0) {
                    if (!(i > 20)) {
                        AudioTrackInfo info = track.getInfo();
                        String title = info.title;
                        if (title == null) title = info.identifier;
                        String duration = Utils.formatDuration(info.length);
                        ebb.appendDescription(String.format("[%d] %s - %s\r\n", i, title, duration));
                        i++;
                    } else {
                        more++;
                    }
                } else {
                    more++;
                }

            }
            if (!(more == 0)) {
                ebb.appendDescription(String.format("And %d more", more));
            }
            message.getChannel().sendMessage(ebb.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return channel.getType().isGuild() || channel.getType() == ChannelType.GROUP;
    }
}
