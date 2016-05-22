package com.speane.tankbattles.server.network.http;

import com.google.gson.Gson;
import com.speane.tankbattles.server.application.LoginInfo;
import com.speane.tankbattles.server.application.RegistrationInfo;
import com.speane.tankbattles.server.application.UserInfo;
import com.speane.tankbattles.server.network.authentication.AuthenticationManager;
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

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class HttpRequestHandleThread implements Runnable {
    private AuthenticationManager authorizationManager;
    private Gson gsonSerializer;
    private HttpRequest request;
    private ResponseSender responseSender;
    private EmailSender emailSender;

    public HttpRequestHandleThread(Socket client, HttpRequest request) throws SQLException, IOException {
        this.request = request;
        emailSender = new EmailSender();
        authorizationManager = new AuthenticationManager();
        gsonSerializer = new Gson();
        responseSender = new ResponseSender(client.getOutputStream());
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
        }
    }

    private void authorizeUser() {
        LoginInfo loginInfo = gsonSerializer.fromJson(new String(request.getMessageBody()), LoginInfo.class);
        UserInfo userInfo = null;
        try {
            userInfo = authorizationManager.getUserInfo(loginInfo.userName, loginInfo.password);
            if (userInfo != null) {
                byte[] userInfoBytes = gsonSerializer.toJson(userInfo).getBytes();
                HttpResponse response = HttpResponseFactory.create(new StatusLine("HTTP/1.1 200 OK"), userInfoBytes);
                try {
                    responseSender.sendResponse(response);
                } catch (IOException e) {
                    System.err.println("Can't send response");
                }
            }
            else {
                try {
                    responseSender.sendNotFoundResponse();
                } catch (IOException e) {
                    System.err.println("Can't send response");
                }
            }
        } catch (SQLException sqlException) {
            try {
                responseSender.sendErrorResponse();
            } catch (IOException e) {
                System.err.println("Can't send response");
            }
        }

    }

    private void registerUser() {
        RegistrationInfo registrationInfo = gsonSerializer.fromJson(new String(request.getMessageBody()),
                RegistrationInfo.class);
        UserInfo userInfo = null;
        try {
            userInfo = authorizationManager.register(registrationInfo.login,
                    registrationInfo.password, registrationInfo.email);
            if (userInfo != null) {
                byte[] userInfoBytes = gsonSerializer.toJson(userInfo).getBytes(Charset.forName("utf-8"));
                HttpResponse httpResponse = new HttpResponse();
                httpResponse.setStatusLine(new StatusLine("HTTP/1.1 200 OK"));
                httpResponse.setMessageBody(userInfoBytes);
                try {
                    responseSender.sendResponse(httpResponse);
                } catch (IOException e) {
                    System.err.println("Can't send response");
                }
                try {
                    emailSender.sendRegistrationConfirmation(registrationInfo);
                } catch (MessagingException e) {
                    System.err.println("Can't send confirmation email");
                }
            }
            else {
                try {
                    responseSender.sendNotFoundResponse();
                } catch (IOException e) {
                    System.err.println("Can't send response");
                }
            }
        } catch (SQLException sqlException) {
            try {
                responseSender.sendErrorResponse();
            } catch (IOException e) {
                System.err.println("Can't send response");
            }
        }
    }
}
