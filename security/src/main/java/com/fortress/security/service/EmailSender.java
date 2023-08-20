package com.fortress.security.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    private final JavaMailSender javaMailSender;

    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(String to, String subject, String message){
        var simpleMailMessage =
                getSimpleMailMessage(to, subject, message);

        javaMailSender.send(simpleMailMessage);

        String successMessage = "Email sent successfully";
    }

    public SimpleMailMessage getSimpleMailMessage(String to,
                                                  String subject,
                                                  String message){

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(to);
        String from = "aegistesteremail@gmail.com";
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        return simpleMailMessage;
    }
}