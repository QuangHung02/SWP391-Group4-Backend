package com.example.pregnancy_tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
public class PregnancyTrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PregnancyTrackingApplication.class, args);
    }
}
