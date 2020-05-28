package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;

public class SeekCommand extends MusicCommand {

    public SeekCommand() {
        super("seek", "seeks to the specified time if possible", "[d:][h:][m:]<s>", "goto", "s");
    }

    @Override
    public boolean execute(ReplyContext replyContext, String... args) {
        if (!this.guildManagerExists(replyContext)) {
            replyContext.reply("no song is currently playing");
            return false;
        } else {
            IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);

            boolean playing = gmm.isPlaying();
            if (playing) {
                if (args.length <= 2) {
                    HelpCommand.sendCommandDescription(replyContext, "empty", "empty", this.getName());
                    return false;
                }
                try {
                    long loc = Utils.StringToDuration(args[2]);
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
                    replyContext.reply("invalid time:",args[2]);
                }
            } else {
                replyContext.reply("no song is currently playing");
            }
            return false;
        }

    }

}
