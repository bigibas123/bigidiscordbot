package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class VolumeCommand extends MusicCommand {
    public VolumeCommand() {
        super("volume", "changes the volume of the currently playing song", "<volume 0-1000>", "v");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (this.guildManagerExists(replyContext)) {
            IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);
            if (args.length > 0) {
                int volume;
                try {
                    volume = Integer.parseInt(args[0]);
                    gmm.setVolume(volume);
                    return true;
                } catch (NumberFormatException e) {
                    replyContext.reply(args[0], "is not a number");
                }
            } else{
                replyContext.reply(gmm.getVolume()+"%");
                return true;
            }
        }
        return false;
    }

    @Override
    protected CommandData _getCommandData(CommandData c) {
        return super._getCommandData(c)
            .addOption(OptionType.INTEGER,"volume","the volume to set the bot to, <0-1000>");
    }

}
