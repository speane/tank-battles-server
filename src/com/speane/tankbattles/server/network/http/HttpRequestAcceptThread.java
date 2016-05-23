package com.speane.tankbattles.server.network.http;

import com.speane.tankbattles.server.help.Config;
import com.speane.tankbattles.server.network.http.request.HttpRequest;
import com.speane.tankbattles.server.network.http.request.RequestReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class HttpRequestAcceptThread implements Runnable {
    private ArrayList<String> onlineUsers;

    public HttpRequestAcceptThread() {
        onlineUsers = new ArrayList<>();
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Config.HTTP_PORT);
            while (true) {
                Socket client = serverSocket.accept();
                HttpRequest httpRequest = new RequestReceiver(client.getInputStream()).getNextRequest();
                new Thread(new HttpRequestHandleThread(client, httpRequest, onlineUsers)).start();
            }
        } catch (IOException | SQLException e) {
            System.err.println(e);
        }
    }

    public ArrayList<String> getOnlineUsers() {
        return onlineUsers;
    }
}
