package com.github.bigibas123.bigidiscordbot.util;

import com.github.bigibas123.bigidiscordbot.Main;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class Utils {
    public static String formatDuration(long dur) {
        long hrs = (dur / 3600000L);
        long mns = (dur / 60000L) % 60000L;
        long scs = dur % 60000L / 1000L;
        if (hrs > 0) {
            return String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            return String.format("%02d:%02d", mns, scs);
        }
    }

    public static boolean isDJ(User user, Guild guild) {
        Member member = guild.getMember(user);
        boolean result;
        if (member == null) {
            result = false;
        } else if (guild.getRolesByName("DJ", true).size() > 0) {
            result = member.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("DJ"));
        } else {
            result = true;
        }
        Main.log.fine(user.getName()+" is DJ "+result);
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
        Main.log.fine(u1+" is "+(result ? "" : " not ")+"the same as "+u2);
        return result;
    }

    public static String getTrackTitle(AudioTrack track){
        String title = track.getInfo().title;
        if (title.equals("Unknown title")) title = track.getIdentifier();
        return title;
    }
}
