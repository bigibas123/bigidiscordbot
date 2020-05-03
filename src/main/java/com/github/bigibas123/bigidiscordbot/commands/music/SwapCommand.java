package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.sound.objects.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import net.dv8tion.jda.api.entities.Message;

public class SwapCommand extends MusicCommand {

	public SwapCommand() {
		super("swap", "swaps the position of two songs in the queue", "<\\#1> <\\#2>", "move");
	}

	@Override
	public boolean execute(Message message, String... args) {

		if (this.guildManagerExists(message)) {
			IGuildMusicManager<?> gmm = this.getGuildManager(message);
			int queueSize = gmm.getQueueSize();
			if (queueSize != 0) {
				if (args.length == 4) {
					//args={@bot, swap, 1, 2}
					int one;
					int two;
					try {
						one = Integer.parseInt(args[2]);
						two = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						message.addReaction(Emoji.WARNING.s()).queue();
						message.getChannel().sendMessage(message.getAuthor().getAsMention() + "error parsing " + e.getMessage() + " this is not a number").queue();
						Main.log.debug("Wrong seeking number input: ", e);
						return false;
					}
					if (one-1 < queueSize && two-1 < queueSize) {
						gmm.swapQueued(one-1, two-1);
						TrackInfo<?> sOne = gmm.getQueuedTrack(one-1);
						TrackInfo<?> sTwo = gmm.getQueuedTrack(two-1);
						message.getChannel().sendMessage(message.getAuthor().getAsMention() + " swaped " + sOne.getTitle() + " with " + sTwo.getTitle()).queue();
						return true;
					} else if (one-1 < queueSize) {
						message.getChannel().sendMessage(message.getAuthor().getAsMention() + " " + two + " is not in the queue, size is: " + queueSize).queue();
						return false;
					} else if (two-1 < queueSize) {
						message.getChannel().sendMessage(message.getAuthor().getAsMention() + " " + one + " is not in the queue, size is: " + queueSize).queue();
						return false;
					} else {
						message.getChannel().sendMessage(message.getAuthor().getAsMention() + " both " + one + " and " + two + " are not in the queue, size is: " + queueSize).queue();
						return false;
					}
				}
				HelpCommand.sendCommandDescription(message, "empty", "empty", "swap");
				return false;
			}
		}
		message.getChannel().sendMessage(message.getAuthor().getAsMention() + " the queue is currently empty").queue();
		return false;

	}

}
