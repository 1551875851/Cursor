package com.mailcursor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MailCursorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailCursorApplication.class, args);
    }
}
