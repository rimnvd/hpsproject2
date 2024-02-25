package ru.itmo.userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender emailSender;

    public void sendRegistrationEmailNotification(String emailTo, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@itmo.ru");
        message.setTo(emailTo);
        message.setSubject("Registration in itmo service");
        message.setText("Hello! Your account with email " + emailTo + " and username " + name + " was successful!");
        emailSender.send(message);
    }
}

