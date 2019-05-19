package ru.nikolay.service.bookBorrowing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.nikolay.service.bookBorrowing", "ru.nikolay.remote", "ru.nikolay.auth"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}