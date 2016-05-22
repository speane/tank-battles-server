package com.speane.tankbattles.server.network.http.request;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Evgeny Shilov on 14.05.2016.
 */
public class RequestReceiver {
    private InputStream inputStream;

    public RequestReceiver(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest getNextRequest() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        HttpRequest request = new HttpRequest();
        String tempLine = dataInputStream.readUTF();
        request.setRequestLine(new RequestLine(tempLine));

        while (!(tempLine = dataInputStream.readUTF()).equals("")) {
            String[] headerParts = tempLine.split(": ");
            request.getHeaders().put(headerParts[0], headerParts[1]);
        }

        if (request.getHeaders().containsKey("Content-Length")) {
            byte[] data = new byte[Integer.parseInt(request.getHeaders().get("Content-Length"))];
            dataInputStream.read(data);
            request.setMessageBody(data);
        }
        return request;
    }
}
