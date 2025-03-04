package ru.sfedu.teamselection.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.dto.student.StudentDto;

@Service
@RequiredArgsConstructor
public class KafkaMessageService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendString(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendStudentDto(String topic, StudentDto studentDto) {
        kafkaTemplate.send(topic, studentDto);
    }

    public void sendApplicationDto(String topic, ApplicationDto applicationDto) {
        kafkaTemplate.send(topic, applicationDto);
    }
}
