package com.github.bigibas123.bigidiscordbot.util;

import com.github.bigibas123.bigidiscordbot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Utils {

    public static String getReactionEmoteLogString(MessageReaction.ReactionEmote emote) {
        if (emote.isEmote()) {
            return ":" + emote.getName() + ":";
        } else {
            return emote.getAsCodepoints();
        }
    }

    public static LocalDateTime idToTime(long id) {
        return LocalDateTime.ofEpochSecond(
                (id >> 22) + 1420070400000L, //Stolen from discord api docs,
                0, ZoneOffset.UTC);
    }

    public static String formatDuration(long dur) {
        Duration duration = Duration.ofMillis(dur);
        long dys = duration.toDays();
        long hrs = duration.toHoursPart();
        long mns = duration.toMinutesPart();
        long scs = duration.toSecondsPart();
        if (dys > 0) {
            return String.format("%02d:%02d:%02d:%02d", dys, hrs, mns, scs);
        } else if (hrs > 0) {
            return String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            return String.format("%02d:%02d", mns, scs);
        }
    }

    public static long StringToDuration(String s) {
        Duration dur = Duration.ZERO;
        String[] parts = s.split(":");
        int li = parts.length - 1;
        if (li >= 0) {
            dur = dur.plusSeconds(Long.parseLong(parts[li]));
        }
        if (li >= 1) {
            dur = dur.plusMinutes(Long.parseLong(parts[li - 1]));
        }
        if (li >= 2) {
            dur = dur.plusHours(Long.parseLong(parts[li - 2]));
        }
        if (li >= 3) {
            dur = dur.plusDays(Long.parseLong(parts[li - 3]));
        }
        return dur.toMillis();
    }

    public static boolean isDJ(User user, Guild guild) {
        Member member = guild.retrieveMember(user).complete();
        boolean result;
        if (member == null) {
            result = false;
        } else if (guild.getRolesByName("DJ", true).size() > 0) {
            result = member.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("DJ"));
        } else {
            result = true;
        }
        Main.log.trace(user.getName() + " is DJ " + result);
        return result;
    }

    // makes really shure shomething isn't the same user
    public static boolean isSameThing(ISnowflake u1, ISnowflake u2) {
        boolean result;
        if (u1 == null || u2 == null) {
            result = false;
        } else if (u1 == u2) {
            result = true;
        } else if (u1.getIdLong() == u2.getIdLong()) {
            result = true;
        } else result = u1.getId().equals(u2.getId());
        Main.log.trace(u1 + " is " + (result ? "" : " not ") + "the same as " + u2);
        return result;
    }

}
