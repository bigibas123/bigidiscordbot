package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.GuildMusicManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.entities.TextChannelImpl;

import java.util.Optional;

public class PlayCommand extends MusicCommand {
    public PlayCommand() {
        super("play", "plays some music in your channel", "[url]", "p");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (message.getChannel().getType() == ChannelType.TEXT) {
            TextChannel chan = ((TextChannelImpl) message.getChannel());
            Optional<VoiceChannel> ovc = chan.getGuild().getVoiceChannels().stream()
                    .filter(c -> c.getMembers().stream()
                            .anyMatch(m -> m.getUser().getId().equals(message.getAuthor().getId()))).findAny();
            if (ovc.isEmpty()) {
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " you need to join a voice channel for this command to work").queue();
                return false;
            }
            VoiceChannel vc = ovc.get();
            GuildMusicManager gmm = this.getGuildManager(message);
            if (args.length <= 2) {
                HelpCommand.sendCommandDescription(message, "empty", "empty", "play");
                return false;
            }
            String search = args[2];
            if (gmm.connect(vc)) {
                gmm.queue(search, message.getTextChannel(), message.getAuthor());
                return true;
            }
        } else {
            message.getChannel().sendMessage("Wrong channel type").queue();
        }
        return false;
    }
}
