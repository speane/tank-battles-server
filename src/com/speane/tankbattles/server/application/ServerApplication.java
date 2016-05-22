package com.speane.tankbattles.server.application;

import com.speane.tankbattles.server.network.http.HttpRequestAcceptThread;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class ServerApplication {
    public void start() {
        new Thread(new HttpRequestAcceptThread()).start();
    }
}
