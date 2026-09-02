package com.practice.student_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${student.message}")
    private String message;

    public void printMessage() {
        System.out.println("Message from configuration: " + message);
    }
}