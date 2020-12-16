package com.github.bigibas123.bigidiscordbot.commands;

import com.github.bigibas123.bigidiscordbot.util.ReplyContext;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;


/**
 * Class representing a command with it's info
 */
@Getter
public abstract class ICommand {

    /**
     * The main name to call the command by
     */
    @NonNull
    private final String name;
    /**
     * Description of what the command does
     */
    @NonNull
    private final String description;
    /**
     * Syntax of the command
     * <br>
     * &lt;arg&gt; denotes a required argument<br>
     * [arg] denotes an option argument
     */
    @NonNull
    private final String syntax;
    /**
     * List of Aliases for the command
     */
    @NonNull
    private final String[] aliases;

    public ICommand(@NonNull String name, @NonNull String description, @NonNull String syntax, String... aliases) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.aliases = Objects.requireNonNullElseGet(aliases, () -> new String[0]);
    }

    public abstract boolean execute(ReplyContext replyContext, String... args);

    public abstract boolean hasPermission(User user, Member member, MessageChannel channel);
}
