package com.speane.tankbattles.server.network.http;

import com.speane.tankbattles.server.network.http.request.HttpRequest;
import com.speane.tankbattles.server.network.http.request.RequestReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class HttpRequestAcceptThread implements Runnable {
    private final int PORT = 8080;

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket client = serverSocket.accept();
                HttpRequest httpRequest = new RequestReceiver(client.getInputStream()).getNextRequest();
                new Thread(new HttpRequestHandleThread(client, httpRequest)).start();
            }
        } catch (IOException e) {
            /*   */
        }
    }


}
