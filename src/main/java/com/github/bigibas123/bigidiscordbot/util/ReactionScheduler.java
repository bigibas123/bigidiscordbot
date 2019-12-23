package com.github.bigibas123.bigidiscordbot.util;

import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.ArrayList;
import java.util.HashMap;

public class ReactionScheduler {
    //TODO make this more robust
    public static HashMap<Long, ArrayList<String>> toFind = new HashMap<>();

    public static void check(MessageReaction reaction) {
        if (toFind.containsKey(reaction.getMessageIdLong())) {
            ArrayList<String> list = toFind.get(reaction.getMessageIdLong());
            list.stream().filter(toRemove -> reaction.getReactionEmote().getName().equals(toRemove))
                    .findFirst().ifPresent(s -> {
                list.remove(s);
                reaction.removeReaction().queue();
            });
            if (list.size() == 0) toFind.remove(reaction.getMessageIdLong(), list);
        }
    }

    public static void scheduleRemoval(long messageID, String emoteName) {
        if (!toFind.containsKey(messageID)) {
            toFind.put(messageID, new ArrayList<>());
        }
        toFind.get(messageID).add(emoteName);
    }
}
