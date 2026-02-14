

package com.notes.notesplatform;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EntityScan("com.notes.notesplatform.model") 
@EnableJpaRepositories("com.notes.notesplatform.repository") 
public class NotesplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesplatformApplication.class, args);
        System.out.println("### Notes Platform Started ###");
    }
}