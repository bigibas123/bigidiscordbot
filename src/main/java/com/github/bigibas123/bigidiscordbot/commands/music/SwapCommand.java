package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.sound.objects.TrackInfo;
import com.github.bigibas123.bigidiscordbot.util.Emoji;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SwapCommand extends MusicCommand {

	public SwapCommand() {
		super("swap", "swaps the position of two songs in the queue", "<\\#1> <\\#2>", "move");
	}

	@Override
	public boolean execute(ReplyContext replyContext, String... args) {

		if (this.guildManagerExists(replyContext)) {
			IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);
			int queueSize = gmm.getQueueSize();
			if (queueSize != 0) {
				if (args.length == 2) {
					//args={1, 2}
					int one;
					int two;
					try {
						one = Integer.parseInt(args[0]);
						two = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						replyContext.reply(Emoji.WARNING);
						replyContext.reply("error parsing ", e.getMessage(), " this is not a number");
						Main.log.debug("Wrong seeking number input: ", e);
						return false;
					}
					if (one - 1 < queueSize && two - 1 < queueSize) {
						gmm.swapQueued(one - 1, two - 1);
						TrackInfo<?> sOne = gmm.getQueuedTrack(one - 1);
						TrackInfo<?> sTwo = gmm.getQueuedTrack(two - 1);
						replyContext.reply("swaped", sOne.getTitle(), "with", sTwo.getTitle());
						return true;
					}
					else if (one - 1 < queueSize) {
						replyContext.reply(two, "is not in the queue, size is:", queueSize);
						return false;
					}
					else if (two - 1 < queueSize) {
						replyContext.reply(one, "is not in the queue, size is:", queueSize);
						return false;
					}
					else {
						replyContext.reply("both", one, "and", two, "are not in the queue, size is:", queueSize);
						return false;
					}
				}
				HelpCommand.sendCommandDescription(replyContext, "swap");
				return false;
			}
		}
		replyContext.reply("the queue is currently empty");
		return false;

	}

	@Override
	protected SlashCommandData _getSlashCommandData(SlashCommandData c) {
		return super._getSlashCommandData(c).addOption(OptionType.INTEGER, "1", "First song in the swap", true).addOption(OptionType.INTEGER, "2", "Second song in the swap", true);
	}

}
