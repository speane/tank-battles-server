package com.speane.tankbattles.server.network.email;


import com.speane.tankbattles.server.network.authentication.RegistrationInfo;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class EmailSender {

    public void sendRegistrationConfirmation(RegistrationInfo registrationInfo) throws MessagingException {
        Properties mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        MimeMessage generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(registrationInfo.email));
        String WELCOME_MESSAGE = "Welcome to Tank Battles";
        generateMailMessage.setSubject(WELCOME_MESSAGE);
        String emailBody = "Welcome to Tank Battles, " + registrationInfo.login
                + ". Your password: " + registrationInfo.password;
        generateMailMessage.setContent(emailBody, "text/html");

        Transport transport = getMailSession.getTransport("smtp");

        String SMTP_HOST = "smtp.gmail.com";
        String SERVER_EMAIL_ID = "tank.battles.mailserver@gmail.com";
        String SERVER_EMAIL_PASSWORD = "123456QWERTY";
        transport.connect(SMTP_HOST, SERVER_EMAIL_ID, SERVER_EMAIL_PASSWORD);
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();
    }
}
