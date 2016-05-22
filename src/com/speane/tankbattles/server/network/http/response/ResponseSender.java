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

    public void sendResponse(HttpResponse response) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(response.getStatusLine().toString());
        for (String tempHeaderKey : response.getHeaders().keySet()) {
            dataOutputStream.writeUTF(tempHeaderKey + ": " + response.getHeaders().get(tempHeaderKey));
        }
        dataOutputStream.writeUTF("");
        if (response.getMessageBody() != null) {
            dataOutputStream.write(response.getMessageBody());
        }
    }

    public void sendErrorResponse() throws IOException {
        sendResponse("HTTP/1.1 500 Server Error");
    }

    public void sendNotFoundResponse() throws IOException {
        sendResponse("HTTP/1.1 404 Not Found");
    }

    public void sendResponse(String responseLine) throws IOException {
        HttpResponse errorResponse = new HttpResponse();
        errorResponse.setStatusLine(new StatusLine(responseLine));
        sendResponse(errorResponse);
    }

    /*public void sendOkResponse(byte[]) {
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine("HTTP/1.1 200 OK"));

    }*/
}
