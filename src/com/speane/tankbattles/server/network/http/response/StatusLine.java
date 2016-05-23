package com.speane.tankbattles.server.network.http.response;

/**
 * Created by Evgeny Shilov on 14.05.2016.
 */
public class StatusLine {
    private String STATUS_LINE_PARTS_DELIMITER = " ";

    private String httpVersion;
    private String statusCode;
    private String reasonMessage;

    public StatusLine() {
        String EMPTY_STRING = "";

        httpVersion = EMPTY_STRING;
        statusCode = EMPTY_STRING;
        reasonMessage = EMPTY_STRING;
    }

    public StatusLine(String statusLine) {
        int HTTP_VERSION_INDEX = 0;
        int STATUS_CODE_INDEX = 1;
        int REASON_MESSAGE_INDEX = 2;

        String[] parts = statusLine.split(STATUS_LINE_PARTS_DELIMITER);
        httpVersion = parts[HTTP_VERSION_INDEX];
        statusCode = parts[STATUS_CODE_INDEX];
        reasonMessage = parts[REASON_MESSAGE_INDEX];
    }

    public String toString() {
        return httpVersion + STATUS_LINE_PARTS_DELIMITER
                + statusCode + STATUS_LINE_PARTS_DELIMITER + reasonMessage;
    }
}
