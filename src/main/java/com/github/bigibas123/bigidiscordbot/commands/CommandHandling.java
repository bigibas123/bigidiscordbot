package com.github.bigibas123.bigidiscordbot.commands;

import com.github.bigibas123.bigidiscordbot.Main;
import com.github.bigibas123.bigidiscordbot.commands.general.ExitCommand;
import com.github.bigibas123.bigidiscordbot.commands.general.HelpCommand;
import com.github.bigibas123.bigidiscordbot.commands.moderation.Prune;
import com.github.bigibas123.bigidiscordbot.commands.music.*;
import com.github.bigibas123.bigidiscordbot.commands.testing.LongRunningCommand;
import com.github.bigibas123.bigidiscordbot.commands.testing.NoPermCommand;
import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.requests.CallbackContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.bigibas123.bigidiscordbot.util.Emoji.*;

public class CommandHandling {

	private static final ArrayList<ICommand> helpList = new ArrayList<>();
	private static final HashMap<String, ICommand> commands = new HashMap<>();

	static {
		registerCommand(new HelpCommand());
		registerCommand(new ExitCommand());
		registerCommand(new Prune());
		registerCommand(new NoPermCommand());
		registerCommand(new LongRunningCommand());
		registerCommand(new PlayCommand());
		registerCommand(new StopCommand());
		registerCommand(new SkipCommand());
		registerCommand(new QueueCommand());
		registerCommand(new SeekCommand());
		registerCommand(new SwapCommand());
		registerCommand(new NowPlayingCommand());
		registerCommand(new VolumeCommand());
		registerCommand(new PauseCommand());
	}

	public CommandHandling() {

	}

	public static void registerCommand(ICommand cmd) {
		helpList.add(cmd);
		commands.put(cmd.getName().toLowerCase(), cmd);
		for (String alias : cmd.getAliases()) {
			commands.put(alias.toLowerCase(), cmd);
		}
	}

	public static void handleSlashRegistration(ReadyEvent event) {

		//		event.getJDA().retrieveCommands().queue(s -> {
		//			s.forEach(cmd -> {
		//				event.getJDA().deleteCommandById(cmd.getIdLong()).queue();
		//			});
		//		});
		event.getJDA().updateCommands().addCommands(getHelpList().stream().map(ICommand::getCommandData).filter(Objects::nonNull).collect(Collectors.toList())).queue();
		//		Guild debugGuild = event.getJDA().getGuildById(232516313099141121L);
		//		assert debugGuild != null;
		//		debugGuild.retrieveCommands()
		//			.queue(s ->
		//				s.parallelStream().forEach(cmd -> debugGuild
		//					.deleteCommandById(cmd.getIdLong())
		//					.queue()
		//				)
		//			);
		//		event.getJDA().getGuildById(232516313099141121L).updateCommands().addCommands(
		//			getHelpList().stream()
		//				.map(ICommand::getCommandData)
		//				.collect(Collectors.toList())
		//		).queue();
	}

	public static ArrayList<ICommand> getHelpList() {
		return helpList;
	}

	public static HashMap<String, ICommand> getCommandList() {
		return commands;
	}

	public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) {
		var opts = event.getOptions().stream().map(OptionMapping::getAsString).toArray(String[]::new);
		var rc = new ReplyContext(event);
		ICommand cmd = commands.get(rc.getSCmdEvent().getName());
		this.handleCommand(cmd, rc, opts);
	}

	public void handleCommand(ICommand cmd, ReplyContext rc, String[] args) {
		Main.log.trace("Starting command thread for: " + (cmd != null ? cmd : "unrecognized command"));
		Main.log.trace("\tcmd args: " + Arrays.toString(args));
		Main.log.trace("\tReplycontext: " + rc.toString());
		new Thread(() -> {
			CallbackContext.getInstance().close();
			if (cmd != null && cmd.hasPermission(rc.getUser(), rc.getMember(), rc.getChannel())) {
				try {
					boolean cmdSuccess = cmd.execute(rc, args);
					if (cmdSuccess) {
						rc.reply(CHECK_MARK);
						Main.log.debug(String.format("User: %s executed %s successfully", rc.getUser(), cmd.getName()));
					} else {
						rc.reply(CROSS);
						Main.log.debug(String.format("User: %s executed %s unsuccessfully", rc.getUser(), cmd.getName()));
					}
				} catch (Throwable e) {
					rc.reply(WARNING);
					Main.log.error("Command failed:", e);
				}
			} else {
				if (cmd != null && !cmd.hasPermission(rc.getUser(), rc.getMember(), rc.getChannel())) {
					rc.reply(STOP_SIGN);
					Main.log.debug(String.format("User: %s got permission denied for %s", rc.getUser(), cmd.getName()));
				} else {
					rc.reply(SHRUG);
					Main.log.debug(String.format("User: %s tried to execute: %s but not found", rc.getUser(), rc.getOriginalText()));
				}

			}
		}).start();
	}

	public void handleChatCommand(Message message) {
		var args = message.getContentRaw().split(" ");
		String scmd;
		if (args.length <= 1) {
			message.addReaction(QUESTION.e()).queue();
			return;
		} else {
			if (args[0].matches("<@(!|&|)\\d{18}>")) {
				scmd = args[1];
				args = Arrays.copyOfRange(args, 2, args.length);
			} else {
				scmd = args[0];
				args = Arrays.copyOfRange(args, 1, args.length);
			}

		}
		ICommand cmd = commands.get(scmd.toLowerCase());
		this.handleCommand(cmd, new ReplyContext(message), args);
	}

}
