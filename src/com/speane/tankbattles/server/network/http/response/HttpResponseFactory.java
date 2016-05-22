package com.speane.tankbattles.server.network.http.response;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class HttpResponseFactory {
    public static HttpResponse create(StatusLine statusLine, byte[] data) {
        HttpResponse response = new HttpResponse();
        response.setStatusLine(statusLine);
        if (data != null) {
            response.setMessageBody(data);
        }
        return response;
    }
}
