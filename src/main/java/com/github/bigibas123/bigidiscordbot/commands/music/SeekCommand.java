package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.entities.Message;

public class SeekCommand extends MusicCommand {

    public SeekCommand() {
        super("seek", "seeks to the specified time if possible", "[d:][h:][m:]<s>", "goto", "s");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (!this.guildManagerExists(message)) {
            message.getChannel().sendMessage(message.getAuthor().getAsMention() + " no song is currently playing").queue();
            return false;
        } else {
            IGuildMusicManager<?> gmm = this.getGuildManager(message);

            boolean playing = gmm.isPlaying();
            if (playing) {
                if (args.length <= 2) {
                    HelpCommand.sendCommandDescription(message, "empty", "empty", this.getName());
                    return false;
                }
                try {
                    long loc = Utils.StringToDuration(args[2]);
                    Main.log.trace(" Trying seeking to: " + loc + " in " + message.getGuild().getName());
                    if (gmm.seek(loc)) {
                        message.addReaction(Emoji.FAST_FORWARD.s()).queue();
                        Main.log.trace("Seeking to: " + loc + " in " + message.getGuild().getName() + " successfull");
                        return true;
                    } else {
                        message.getChannel().sendMessage(message.getAuthor().getAsMention() + " can not seek on track: " + gmm.getCurrentTrack().getTitle()).queue();
                        Main.log.debug("Seeking to: " + loc + " in " + message.getGuild().getName() + " failed");
                    }
                } catch (NumberFormatException e) {
                    message.getChannel().sendMessage(message.getAuthor().getAsMention() + " invalid time: " + args[2]).queue();
                }
            } else {
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " no song is currently playing").queue();
            }
            return false;
        }

    }

}
