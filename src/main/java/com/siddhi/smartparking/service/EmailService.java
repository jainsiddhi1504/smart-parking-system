package com.siddhi.smartparking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(
            JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
    }

    public void sendEmail(
            String to,
            String subject,
            String body
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        System.out.println(
                "EMAIL SENT TO: " + to
        );
    }

    public void sendEmailWithAttachment(
            String to,
            String subject,
            String body,
            File attachment
    ) throws Exception {

        MimeMessage message =
                mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        true
                );

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        FileSystemResource file =
                new FileSystemResource(
                        attachment
                );

        helper.addAttachment(
                attachment.getName(),
                file
        );

        try {
            mailSender.send(message);
            System.out.println("EMAIL SENT SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        System.out.println(
                "EMAIL WITH PDF SENT TO: "
                        + to
        );
    }
}