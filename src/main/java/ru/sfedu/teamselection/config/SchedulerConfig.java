package ru.sfedu.teamselection.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true")
public class SchedulerConfig {
    public final String studentTrackPeriod;

    public SchedulerConfig(
        @Value("${app.scheduler.studentTrack}") String studentTrackPeriod
    ) {

        this.studentTrackPeriod = studentTrackPeriod;
    }
}
