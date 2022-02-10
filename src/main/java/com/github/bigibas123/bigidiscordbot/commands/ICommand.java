package com.github.bigibas123.bigidiscordbot.commands;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.*;


/**
 * Class representing a command with it's info
 */
@Getter public abstract class ICommand {

	/**
	 * The main name to call the command by
	 */
	@NonNull private final String name;
	/**
	 * Description of what the command does
	 */
	@NonNull private final String description;
	/**
	 * Syntax of the command
	 * <br>
	 * &lt;arg&gt; denotes a required argument<br>
	 * [arg] denotes an option argument
	 */
	@NonNull private final String syntax;
	/**
	 * List of Aliases for the command
	 */
	@NonNull private final String[] aliases;

	public ICommand(@NonNull String name, @NonNull String description, @NonNull String syntax, String... aliases) {
		this.name = name;
		this.description = description;
		this.syntax = syntax;
		this.aliases = Objects.requireNonNullElseGet(aliases, () -> new String[0]);
	}

	public abstract boolean execute(ReplyContext replyContext, String... args);

	public abstract boolean hasPermission(User user, Member member, MessageChannel channel);

	public final CommandData getCommandData() {
		return this._getSlashCommandData(Commands.slash(getName().toLowerCase(Locale.ROOT), getDescription()));
	}

	protected abstract SlashCommandData _getSlashCommandData(SlashCommandData c);

	public final Collection<CommandPrivilege> getPrivileges(Guild g) {
		Main.log.trace("Getting Privileges for: " + g.getIdLong() + " in command " + this.getName());
		var list = new LinkedList<CommandPrivilege>();
		var roles = g.getRoles();
		var returnedList = this._getPrivilegesForGuild(g, roles, list);
		Main.log.trace("Priveleges for: " + g.getIdLong() + " in command: " + this.getName() + ": " + (returnedList != null ? returnedList : list).stream().map(p -> String.format("{t:%s,id:%s}",
																																												   p.getType(),
																																												   p.getIdLong()
		)).toList().toString());
		return returnedList != null ? returnedList : list;
	}

	protected abstract Collection<CommandPrivilege> _getPrivilegesForGuild(Guild g, List<Role> roles, List<CommandPrivilege> list);

}
