package com.homecraft.api.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendContactMail(String from, String message) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo("support@homecraft.com");
        mail.setSubject("New Contact Message - HomeCraft");

        mail.setText(
                "Sender: " + from + "\n\n" +
                        "Message:\n" +
                        message
        );

        mailSender.send(mail);
    }
}