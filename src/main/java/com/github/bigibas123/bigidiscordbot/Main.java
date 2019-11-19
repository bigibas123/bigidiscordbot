package com.github.bigibas123.bigidiscordbot;


import com.github.bigibas123.bigidiscordbot.sound.SoundManager;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.factory.DefaultSendSystem;

import javax.security.auth.login.LoginException;
import java.util.logging.Logger;

public class Main {

    public static Logger log;
    public static SoundManager soundManager;
    @Getter
    private static JDA jda;

    public static void main(String[] args) throws LoginException {
        log = Logger.getLogger(Main.class.getCanonicalName());
        soundManager = new SoundManager();
        jda = new JDABuilder(Reference.token)
                .addEventListeners(new Listener())
                .setAudioSendFactory(DefaultSendSystem::new)
                .setBulkDeleteSplittingEnabled(true)
                .build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
