package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.ICommand;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import com.github.bigibas123.bigidiscordbot.util.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class MusicCommand extends ICommand {

	public MusicCommand(String name, String description, String syntax, String... aliases) {
		super(name, description, syntax, aliases);
	}

	protected IGuildMusicManager<?> getGuildManager(ReplyContext replyContext) {
		return Main.soundManager.getGuildMusicManager(replyContext.getGuild());
	}

	protected boolean guildManagerExists(ReplyContext replyContext) {
		return Main.soundManager.guildMusicManagerExists(replyContext.getGuild());
	}

	protected void stopGuildManager(ReplyContext replyContext) {
		Main.soundManager.removeGuildMusicManager(replyContext.getGuild());
	}

	@Override
	public boolean hasPermission(User user, Member member, MessageChannelUnion channel) {
		if (channel.getType().isGuild()) {
			if (channel.getType().isMessage()) {
				return Utils.isDJ(user, channel.asGuildMessageChannel().getGuild());
			} else {
				return false;
			}
		} else {
			return channel.getType() == ChannelType.PRIVATE;
		}
	}

	@Override
	protected SlashCommandData _getSlashCommandData(SlashCommandData c) {
		return c.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
	}

}
