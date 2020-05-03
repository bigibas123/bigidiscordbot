package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;

public class VolumeCommand extends MusicCommand {
    public VolumeCommand() {
        super("volume", "changes the volume of the currently playing song", "<volume 0-100>", "v");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (this.guildManagerExists(replyContext)) {
            if (args.length > 2) {
                IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);
                int volume;
                try {
                    volume = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    replyContext.reply(args[2],"is not a number");
                    return false;
                }
                gmm.setVolume(volume);
                return true;
            }
            replyContext.reply("please specify a volume");
            HelpCommand.sendCommandDescription(replyContext, null, null, "volume");
        }
        return false;
    }

}
