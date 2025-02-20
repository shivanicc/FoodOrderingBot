package org.example;

import org.example.Bot;
import org.example.DummyHttpServer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Start the HTTP server for Cloud Run health checks in a separate thread.
        new Thread(() -> {
            try {
                DummyHttpServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Existing code to initialize and register your bot.
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
            System.out.println("Bot is running...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
