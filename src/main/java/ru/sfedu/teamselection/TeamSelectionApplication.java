package ru.sfedu.teamselection;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
class TeamSelectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeamSelectionApplication.class, args);
    }
}
