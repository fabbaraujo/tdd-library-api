package com.github.fabbaraujo.libraryapi.service;

import java.util.List;

public interface EmailService {
    void sendEmail(String message, List<String> emailsList);
}
