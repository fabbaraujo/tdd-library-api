package com.github.fabbaraujo.libraryapi.service.impl;

import com.github.fabbaraujo.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-remetent}")
    private String remetent;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String message, List<String> emailsList) {
        String[] mails = emailsList.toArray(new String[emailsList.size()]);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("Livro com empréstimo atrasado.");
        mailMessage.setText(message);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);
    }
}
