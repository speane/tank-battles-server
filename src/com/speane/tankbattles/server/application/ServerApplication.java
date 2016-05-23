package com.speane.tankbattles.server.application;

import com.speane.tankbattles.server.network.http.HttpRequestAcceptThread;

import java.io.IOException;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class ServerApplication {
    public void start() {
        new Thread(new HttpRequestAcceptThread()).start();
        new Thread(() -> {
            try {
                new PlayServer().start();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
