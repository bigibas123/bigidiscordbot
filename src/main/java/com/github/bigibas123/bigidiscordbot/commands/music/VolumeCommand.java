package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import net.dv8tion.jda.api.entities.Message;

public class VolumeCommand extends MusicCommand {
    public VolumeCommand() {
        super("volume", "changes the volume of the currently playing song", "<volume 0-100>", "v");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (this.guildManagerExists(message)) {
            if (args.length > 2) {
                IGuildMusicManager gmm = this.getGuildManager(message);
                int volume;
                try {
                    volume = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    message.getChannel().sendMessage(message.getAuthor().getAsMention() + " " + args[2] + " is not a number").queue();
                    return false;
                }
                gmm.setVolume(volume);
                return true;
            }
            message.getChannel().sendMessage(message.getAuthor().getAsMention() + " please specify a volume").queue();
            HelpCommand.sendCommandDescription(message, null, null, "volume");
        }
        return false;
    }

}
