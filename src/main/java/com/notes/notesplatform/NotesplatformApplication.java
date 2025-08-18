package com.notes.notesplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotesplatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotesplatformApplication.class, args);
        System.out.println("### Notes Platform Started ###");
    }
}