package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;

public class VolumeCommand extends MusicCommand {
    public VolumeCommand() {
        super("volume", "changes the volume of the currently playing song", "<volume 0-1000>", "v");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (this.guildManagerExists(replyContext)) {
            IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);
            if (args.length > 2) {
                int volume;
                try {
                    volume = Integer.parseInt(args[2]);
                    gmm.setVolume(volume);
                    return true;
                } catch (NumberFormatException e) {
                    replyContext.reply(args[2], "is not a number");
                }
            } else if(args.length > 1){
                replyContext.reply(gmm.getVolume()+"‰");
                return true;
            }
        }
        return false;
    }

}
