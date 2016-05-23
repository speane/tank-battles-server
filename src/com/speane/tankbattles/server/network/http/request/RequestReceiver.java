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

        String EMPTY_STRING = "";
        String HEADER_DELIMITER = ": ";
        int HEADER_NAME_INDEX = 0;
        int HEADER_VALUE_INDEX = 1;
        while (!(tempLine = dataInputStream.readUTF()).equals(EMPTY_STRING)) {
            String[] headerParts = tempLine.split(HEADER_DELIMITER);
            request.getHeaders().put(headerParts[HEADER_NAME_INDEX], headerParts[HEADER_VALUE_INDEX]);
        }
        String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
        if (request.getHeaders().containsKey(CONTENT_LENGTH_HEADER_NAME)) {
            byte[] data = new byte[Integer.parseInt(request.getHeaders().get(CONTENT_LENGTH_HEADER_NAME))];
            dataInputStream.read(data);
            request.setMessageBody(data);
        }
        return request;
    }
}
