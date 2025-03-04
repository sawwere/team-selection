package ru.sfedu.teamselection.controller.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.dto.student.StudentDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaStudentListener {

    @KafkaListener(topics = "team-selection.students.created")
    @RetryableTopic(attempts = "3", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    public void listenCreatedStudent(StudentDto studentDto) {
        log.info("Accept student.created message. Sending email to {}", studentDto.getUser().getEmail());
    }
}
