package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import net.dv8tion.jda.api.entities.Message;

public class PauseCommand extends MusicCommand {
    public PauseCommand() {
        super("pause", "pauses the currently playing song", "", "resume");
    }

    @Override
    public boolean execute(Message message, String... args) {
        if (!this.guildManagerExists(message)) {
            message.getChannel().sendMessage(message.getAuthor().getAsMention() + " no song is currently playing").queue();
            return false;
        } else {
            IGuildMusicManager gmm = this.getGuildManager(message);

            boolean playing = gmm.isPlaying();
            if (playing) {
                gmm.pause();
                message.addReaction(Emoji.PAUSE.s()).queue();
            } else {
                gmm.unpause();
                message.addReaction(Emoji.PLAY.s()).queue();
            }
            return true;
        }

    }
}
