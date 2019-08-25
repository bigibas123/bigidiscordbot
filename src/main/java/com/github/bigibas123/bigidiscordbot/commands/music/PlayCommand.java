package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.GuildMusicManager;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;

import java.util.Optional;

public class PlayCommand extends ICommand {
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
            GuildMusicManager gmm = Main.soundManager.getGuildMusicManager(message.getGuild());
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

    @Override
    public boolean hasPermission(User user, MessageChannel channel) {
        return channel.getType().isGuild();
    }
}
