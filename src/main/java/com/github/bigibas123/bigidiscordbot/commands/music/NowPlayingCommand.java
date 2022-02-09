package com.github.bigibas123.bigidiscordbot.commands.music;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.sound.IGuildMusicManager;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Collection;
import java.util.List;

public class NowPlayingCommand extends MusicCommand {

	public NowPlayingCommand() {
		super("nowplaying", "shows the currently playing song", "", "np");
	}

	@Override
	public boolean execute(ReplyContext replyContext, String... args) {
		if (this.guildManagerExists(replyContext)) {
			IGuildMusicManager<?> gmm = this.getGuildManager(replyContext);
			if (gmm.isPlaying()) {
				Main.log.trace("Guild Playing Status: " + gmm.isPlaying());
				String title = gmm.getCurrentTrack().getTitle();
				replyContext.reply("Currently playing:", title);
				return true;
			}
		}
		replyContext.reply("no song is currently playing");
		return false;
	}

	@Override
	public boolean hasPermission(User user, Member member, MessageChannel channel) {
		return channel.getType().isGuild() || channel.getType() == ChannelType.GROUP;
	}

	@Override
	protected Collection<CommandPrivilege> _getPrivilegesForGuild(Guild g, List<Role> roles, List<CommandPrivilege> list) {
		list.add(CommandPrivilege.enable(g.getPublicRole()));
		return list;
	}

}
