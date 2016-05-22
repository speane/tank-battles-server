package com.speane.tankbattles.server.network.http.request;

import com.speane.tankbattles.server.network.http.HttpMessage;

/**
 * Created by Evgeny Shilov on 14.05.2016.
 */
public class HttpRequest extends HttpMessage {
    public static final String POST = "POST";
    public static final String GET = "GET";

    private RequestLine requestLine;

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }
}
