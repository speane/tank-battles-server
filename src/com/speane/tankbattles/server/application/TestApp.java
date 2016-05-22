package com.speane.tankbattles.server.application;

import com.google.gson.Gson;
import com.speane.tankbattles.server.network.http.request.HttpRequest;
import com.speane.tankbattles.server.network.http.request.RequestReceiver;
import com.speane.tankbattles.server.network.http.response.HttpResponse;
import com.speane.tankbattles.server.network.http.response.ResponseSender;
import com.speane.tankbattles.server.network.http.response.StatusLine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class TestApp {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(8080);
                } catch (IOException e) {

                }
                while (true) {
                    try {
                        Socket client = serverSocket.accept();
                        HttpRequest request = new RequestReceiver(client.getInputStream()).getNextRequest();
                        System.out.println(new Gson().fromJson(new String(request.getMessageBody()), LoginInfo.class).password);
                        HttpResponse response = new HttpResponse();
                        response.setStatusLine(new StatusLine("HTTP/1.1 200 OK"));
                        response.getHeaders().put("Host", serverSocket.getInetAddress().getHostName());
                        UserInfo userInfo = new UserInfo();
                        userInfo.name = "HAMMOND";
                        response.setMessageBody(new Gson().toJson(userInfo).getBytes(Charset.forName("utf-8")));
                        new ResponseSender(client.getOutputStream()).sendResponse(response);
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            }
        }).start();
    }
}
