package com.speane.tankbattles.server.network.http.request;

/**
 * Created by Evgeny Shilov on 14.05.2016.
 */
public class RequestLine {
    private String method;
    private String URI;
    private String httpVersion;

    public RequestLine() {
        method = "";
        URI = "";
        httpVersion = "";
    }

    public RequestLine(String requestLine) {
        String[] lineParts = requestLine.split(" ");
        method = lineParts[0];
        URI = lineParts[1];
        httpVersion = lineParts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getURI() {
        return URI;
    }

    public String toString() {
        return method + " " + URI + " " + httpVersion;
    }
}
