package com.speane.tankbattles.server.network.http;

import com.google.gson.Gson;
import com.speane.tankbattles.server.network.authentication.AuthenticationManager;
import com.speane.tankbattles.server.network.authentication.LoginInfo;
import com.speane.tankbattles.server.network.authentication.RegistrationInfo;
import com.speane.tankbattles.server.network.authentication.UserInfo;
import com.speane.tankbattles.server.network.email.EmailSender;
import com.speane.tankbattles.server.network.http.request.HttpRequest;
import com.speane.tankbattles.server.network.http.response.HttpResponse;
import com.speane.tankbattles.server.network.http.response.HttpResponseFactory;
import com.speane.tankbattles.server.network.http.response.ResponseSender;
import com.speane.tankbattles.server.network.http.response.StatusLine;
import com.speane.tankbattles.server.network.http.uri.URITypes;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class HttpRequestHandleThread implements Runnable {
    private final String OK_STATUS_LINE_TEXT = "HTTP/1.1 200 OK";

    private AuthenticationManager authorizationManager;
    private Gson gsonSerializer;
    private HttpRequest request;
    private ResponseSender responseSender;
    private EmailSender emailSender;

    private ArrayList<String> onlineUsers;

    public HttpRequestHandleThread(Socket client, HttpRequest request, ArrayList<String> onlineUsers)
            throws SQLException, IOException {
        this.request = request;
        emailSender = new EmailSender();
        authorizationManager = new AuthenticationManager();
        gsonSerializer = new Gson();
        responseSender = new ResponseSender(client.getOutputStream());
        this.onlineUsers = onlineUsers;
    }

    @Override
    public void run() {
        handleRequest();
    }

    private void handleRequest() {
        switch (request.getRequestLine().getMethod()) {
            case HttpRequest.POST:
                postRequestHandle();
                break;
        }
    }

    private void postRequestHandle() {
        String URI = request.getRequestLine().getURI();
        System.out.println(URI);
        switch (URI) {
            case URITypes.AUTHORIZATION:
                authorizeUser();
                break;
            case URITypes.REGISTRATION:
                registerUser();
                break;
            case URITypes.UPDATE_USER_INFO:
                updateUserInfo();
                break;
        }
    }

    private void updateUserInfo() {
        UserInfo userInfo = gsonSerializer.fromJson(new String(request.getMessageBody()), UserInfo.class);
        try {
            userInfo = authorizationManager.updateUserInfo(userInfo);
            if (userInfo != null) {
                byte[] userInfoBytes = gsonSerializer.toJson(userInfo).getBytes();
                HttpResponse response = HttpResponseFactory.create(new StatusLine(OK_STATUS_LINE_TEXT), userInfoBytes);
                responseSender.sendResponse(response);
            }
            else {
                responseSender.sendErrorResponse();
            }
        } catch (SQLException sqlException) {
            responseSender.sendErrorResponse();
        }
    }

    private void authorizeUser() {
        LoginInfo loginInfo = gsonSerializer.fromJson(new String(request.getMessageBody()), LoginInfo.class);
        UserInfo userInfo;
        try {
            userInfo = authorizationManager.getUserInfo(loginInfo.userName, loginInfo.password);
            if (userInfo != null) {
                if (!onlineUsers.contains(userInfo.name)) {
                    onlineUsers.add(userInfo.name);
                    byte[] userInfoBytes = gsonSerializer.toJson(userInfo).getBytes();
                    HttpResponse response = HttpResponseFactory.create(new StatusLine(OK_STATUS_LINE_TEXT),
                            userInfoBytes);
                    responseSender.sendResponse(response);
                }
                else {
                    String USER_ALREADY_AUTHORIZED_STATUS_LINE_TEXT = "HTTP/1.1 407 Already Authorized";
                    HttpResponse response = HttpResponseFactory.create(
                            new StatusLine(USER_ALREADY_AUTHORIZED_STATUS_LINE_TEXT), new byte[0]);
                    responseSender.sendResponse(response);
                }
            }
            else {
                responseSender.sendNotFoundResponse();
            }
        } catch (SQLException sqlException) {
            responseSender.sendErrorResponse();
        }

    }

    private void registerUser() {
        RegistrationInfo registrationInfo = gsonSerializer.fromJson(new String(request.getMessageBody()),
                RegistrationInfo.class);
        UserInfo userInfo;
        try {
            if (authorizationManager.getUserInfo(registrationInfo.login, registrationInfo.password) != null) {
                String USER_EXISTS_RESPONSE_STATUS_LINE = "HTTP/1.1 405 Exists";
                responseSender.sendResponse(USER_EXISTS_RESPONSE_STATUS_LINE);
            }
            else {
                userInfo = authorizationManager.register(registrationInfo.login,
                        registrationInfo.password, registrationInfo.email);
                if (userInfo != null) {
                    String DEFAULT_CHARSET = "utf-8";
                    byte[] userInfoBytes = gsonSerializer.toJson(userInfo).getBytes(Charset.forName(DEFAULT_CHARSET));
                    HttpResponse httpResponse = new HttpResponse();
                    httpResponse.setStatusLine(new StatusLine(OK_STATUS_LINE_TEXT));
                    httpResponse.setMessageBody(userInfoBytes);
                    responseSender.sendResponse(httpResponse);
                    try {
                        emailSender.sendRegistrationConfirmation(registrationInfo);
                    } catch (MessagingException e) {
                        System.err.println(e);
                    }
                }
                else {
                    responseSender.sendErrorResponse();
                }
            }
        } catch (SQLException sqlException) {
            responseSender.sendErrorResponse();
        }
    }
}
