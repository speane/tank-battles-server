package com.speane.tankbattles.server.network.http.response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Evgeny Shilov on 14.05.2016.
 */
public class ResponseSender {
    private OutputStream outputStream;

    public ResponseSender(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendResponse(HttpResponse response) {
        String EMPTY_STRING = "";
        String HEADER_DELIMITER = ": ";
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(response.getStatusLine().toString());
            for (String tempHeaderKey : response.getHeaders().keySet()) {
                dataOutputStream.writeUTF(tempHeaderKey + HEADER_DELIMITER + response.getHeaders().get(tempHeaderKey));
            }
            dataOutputStream.writeUTF(EMPTY_STRING);
            if (response.getMessageBody() != null) {
                dataOutputStream.write(response.getMessageBody());
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void sendErrorResponse() {
        String SERVER_ERROR_RESPONSE_STATUS_LINE = "HTTP/1.1 500 Server Error";
        sendResponse(SERVER_ERROR_RESPONSE_STATUS_LINE);
    }

    public void sendNotFoundResponse() {
        String NOT_FOUND_RESPONSE_STATUS_LINE = "HTTP/1.1 404 Not Found";
        sendResponse(NOT_FOUND_RESPONSE_STATUS_LINE);
    }

    public void sendResponse(String responseLine) {
        HttpResponse errorResponse = new HttpResponse();
        errorResponse.setStatusLine(new StatusLine(responseLine));
        sendResponse(errorResponse);
    }
}
