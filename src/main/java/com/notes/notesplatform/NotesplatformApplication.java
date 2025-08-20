/*package com.notes.notesplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotesplatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotesplatformApplication.class, args);
        System.out.println("### Notes Platform Started ###");
    }
}*/

package com.notes.notesplatform;

// Import two new annotations
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EntityScan("com.notes.notesplatform.model") // Added this line to scan for entities
@EnableJpaRepositories("com.notes.notesplatform.repository") // Added this line to scan for repositories
public class NotesplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesplatformApplication.class, args);
        System.out.println("### Notes Platform Started ###");
    }
}