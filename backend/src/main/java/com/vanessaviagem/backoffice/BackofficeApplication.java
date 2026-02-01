package com.vanessaviagem.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Vanessa Viagem Backoffice.
 * Manages customer loyalty miles programs including purchases, sales, and bonuses.
 */
@SpringBootApplication
public class BackofficeApplication {

    /**
     * Private constructor to satisfy Checkstyle HideUtilityClassConstructor.
     * Spring Boot requires this class to be instantiable for context initialization.
     */
    protected BackofficeApplication() {
        // Protected constructor for Spring Boot instantiation
    }

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BackofficeApplication.class, args);
    }
}
