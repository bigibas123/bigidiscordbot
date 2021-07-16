package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SeekCommand extends MusicCommand {

    public SeekCommand() {
        super("seek", "seeks to the specified time if possible", "[d:][h:][m:]<s>", "goto", "s");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (this.guildManagerExists(replyContext)) {
            IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);

            boolean playing = gmm.isPlaying();
            if (playing) {
                if (args.length < 1) {
                    HelpCommand.sendCommandDescription(replyContext, this.getName());
                    return false;
                }
                try {
                    long loc = Utils.StringToDuration(args[0]);
                    Main.log.trace(" Trying seeking to: " + loc + " in " + replyContext.getGuild().getName());
                    if (gmm.seek(loc)) {
                        replyContext.reply(Emoji.FAST_FORWARD);
                        Main.log.trace("Seeking to: " + loc + " in " + replyContext.getGuild().getName() + " successfull");
                        return true;
                    } else {
                        replyContext.reply("Can not seek on track:",gmm.getCurrentTrack().getTitle());
                        Main.log.debug("Seeking to: " + loc + " in " + replyContext.getGuild().getName() + " failed");
                    }
                } catch (NumberFormatException e) {
                    replyContext.reply("invalid time:",args[0]);
                }
            } else {
                replyContext.reply("no song is currently playing");
            }
        } else {
            replyContext.reply("no song is currently playing");
        }
        return false;
    }

    @Override
    protected CommandData _getCommandData(CommandData c) {
        return super._getCommandData(c)
            .addOption(OptionType.STRING,"time","time to seek to, [[[day:]hour:]minute:]<second>",true);
    }

}
