package ru.sfedu.teamselection.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class TopicConfiguration {
    @Bean
    public NewTopic studentsTopic() {
        return new NewTopic(
                "team-selection.students.created_dlq",
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic applicationAcceptedTopic() {
        return new NewTopic(
                "team-selection.applications.accepted_dlq",
                2,
                (short) 1
        );
    }

    @Bean
    public NewTopic applicationRejectedTopic() {
        return new NewTopic(
                "team-selection.applications.rejected_dlq",
                2,
                (short) 1
        );
    }
}
