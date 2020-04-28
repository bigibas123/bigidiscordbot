package com.github.bigibas123.bigidiscordbot.util;

import net.dv8tion.jda.api.entities.MessageReaction;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ReactionScheduler {

    //TODO make this more robust
    private static final HashMap<Long, ArrayList<String>> toFind = new HashMap<>();
    private static final HashMap<Long, MessageReaction> reactions = new HashMap<>();

    public static void check(MessageReaction reaction) {
        reactions.put(reaction.getMessageIdLong(), reaction);
        if (toFind.containsKey(reaction.getMessageIdLong())) {
            ArrayList<String> list = toFind.get(reaction.getMessageIdLong());
            list.stream().filter(toRemove -> reaction.getReactionEmote().getName().equals(toRemove))
                    .findFirst().ifPresent(s -> {
                list.remove(s);
                reaction.removeReaction().queue();
            });
            if (list.size() == 0) toFind.remove(reaction.getMessageIdLong(), list);
        }
        cleanLists();
    }

    private static void cleanLists() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
        reactions.keySet().removeIf(m -> Utils.idToTime(m).isBefore(cutoff));
        toFind.keySet().removeIf(m -> Utils.idToTime(m).isBefore(cutoff));
    }

    public static void scheduleRemoval(long messageID, String emoteName) {
        MessageReaction msg;
        if ((msg = reactions.get(messageID)) != null && msg.getReactionEmote().getName().equals(emoteName)) {
            msg.removeReaction().queue();
        } else {
            if (!toFind.containsKey(messageID)) {
                toFind.put(messageID, new ArrayList<>());
            }
            toFind.get(messageID).add(emoteName);
        }
        cleanLists();
    }

}
