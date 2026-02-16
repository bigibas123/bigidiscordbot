package com.github.bigibas123.bigidiscordbot.util;

import java.util.HashMap;
import java.util.Map;

//https://itpro.cz/juniconv/
public enum Emoji {
	STOP_SIGN("\uD83D\uDED1"),  	//🛑
	RUNNER("\uD83C\uDFC3"),     	//🏃
	CHECK_MARK("✅"),       		//✅
	CROSS("❌"),            		//❌
	STOP_WATCH("⏱"),       		//⏱
	QUESTION("❓"),       	  	//❓
	SHRUG("\uD83E\uDD37"),      	//🤷
	WAVE("\uD83D\uDC4B"),       	//👋
	PAUSE("⏸"),            		//⏸
	PLAY("▶"),             		//▶
	FAST_FORWARD("⏩"),     		//⏩
	STOP("⏹"),             		//⏹
	ONE("1\u20E3"),        		//1️⃣
	TWO("2\u20E3"),        		//2️⃣
	THREE("3\u20E3"),      		//3️⃣
	FOUR("4\u20E3"),       		//4️⃣
	FIVE("5\u20E3"),       		//5️⃣
	SIX("6\u20E3"),        		//6️⃣
	SEVEN("7\u20E3"),      		//7️⃣
	EIGHT("8\u20E3"),      		//8️⃣
	NINE("9\u20E3"),       		//9️⃣
	TEN("\uD83D\uDD1F"),        	//🔟
	WARNING("⚠")           		//⚠️
	;

	public static final Map<Integer, Emoji> oneToTen = new HashMap<>();

	static {
		oneToTen.put(1, ONE);
		oneToTen.put(2, TWO);
		oneToTen.put(3, THREE);
		oneToTen.put(4, FOUR);
		oneToTen.put(5, FIVE);
		oneToTen.put(6, SIX);
		oneToTen.put(7, SEVEN);
		oneToTen.put(8, EIGHT);
		oneToTen.put(9, NINE);
		oneToTen.put(10, TEN);
	}

	private final String toString;

	Emoji(String s) {
		this.toString = s;
	}

	public String s() {
		return this.toString;
	}

	public net.dv8tion.jda.api.entities.emoji.EmojiUnion e() {
		return net.dv8tion.jda.api.entities.emoji.Emoji.fromFormatted(this.toString);
	}

	@Override
	public String toString() {
		return this.toString;
	}
}
