package com.speane.tankbattles.server.network.http.response;

import com.speane.tankbattles.server.network.http.HttpMessage;

/**
 * Created by Evgeny Shilov on 14.05.2016.
 */
public class HttpResponse extends HttpMessage {
    private StatusLine statusLine;

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }
}
