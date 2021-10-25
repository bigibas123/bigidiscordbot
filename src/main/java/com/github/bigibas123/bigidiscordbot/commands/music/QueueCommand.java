package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.sound.objects.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Collection;
import java.util.List;

public class QueueCommand extends MusicCommand {
    public QueueCommand() {
        super("queue", "list all queued songs for this guild", "", "qeueu");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (this.guildManagerExists(replyContext)) {
            IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);
            List<? extends TrackInfo<?>> tracks = gmm.getQueued();
            EmbedBuilder ebb = new EmbedBuilder();
            ebb.setTitle(String.format("Queue for %s", replyContext.getGuild().getName()));
            ebb.setFooter(String.format("Requested by @%s", replyContext.getUser().getName()), replyContext.getUser().getEffectiveAvatarUrl());
            int i = 1;
            int more = 0;

            for (TrackInfo<?> track : tracks) {
                if (more == 0) {
                    if (!(i > 20)) {
                        String title = track.getTitle();
                        String duration = Utils.formatDuration(track.getDuration());
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
            replyContext.reply(ebb.build());
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(User user, Member member, MessageChannel channel) {
        return channel.getType().isGuild() || channel.getType() == ChannelType.GROUP;
    }

    @Override
    protected Collection<? extends CommandPrivilege> _getPrivilegesForGuild(Guild g, List<Role> roles, List<CommandPrivilege> list) {
        list.add(CommandPrivilege.enable(g.getPublicRole()));
        return list;
    }

}
