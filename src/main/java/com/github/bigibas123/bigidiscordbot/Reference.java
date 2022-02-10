package com.github.bigibas123.bigidiscordbot;

public class Reference {
	public static final String token = System.getenv("DISCORD_TOKEN");
	public static final String ownerID = System.getenv("OWNER_ID");
	public static final boolean varsSet;

	static {
		boolean tN = token == null;
		boolean oN = ownerID == null;
		if (tN || oN) {
			varsSet = false;
			if (tN) {
				Main.log.error("Discord Bot Token not set, please set DISCORD_TOKEN in environment");
			}
			if (oN) {
				Main.log.error("Owner ID not set, please set OWNER_ID in environment");
			}
		}
		else {
			varsSet = true;
		}
	}
}
