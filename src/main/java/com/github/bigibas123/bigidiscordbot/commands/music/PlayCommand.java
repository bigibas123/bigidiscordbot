package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.entities.TextChannelImpl;

import java.util.Arrays;
import java.util.Optional;

public class PlayCommand extends MusicCommand {
    public PlayCommand() {
        super("play", "plays some music in your voicechannel", "<url|ytsearch:|scsearch:> [search_terms]", "p");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (replyContext.getChannel().getType() == ChannelType.TEXT) {
            TextChannel chan = ((TextChannelImpl) replyContext.getChannel());
            Optional<VoiceChannel> ovc = chan.getGuild().getVoiceChannels().stream()
                    .filter(c -> c.getMembers().stream()
                            .anyMatch(m -> Utils.isSameThing(m.getUser(),replyContext.getUser()))).findAny();
            if (ovc.isEmpty()) {
                replyContext.reply("you need to join a voice channel for this command to work");
                return false;
            }
            if (args.length <= 2) {
                HelpCommand.sendCommandDescription(replyContext, "empty", "empty", "play");
                return false;
            }
            VoiceChannel vc = ovc.get();
            IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);
            String search = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            if (gmm.connect(vc)) {
                gmm.queue(search, replyContext);
                return true;
            }
        } else {
            replyContext.reply("Wrong channel type");
        }
        return false;
    }
}
