package ru.sfedu.teamselection.controller.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.mapper.application.ApplicationDtoMapper;
import ru.sfedu.teamselection.service.StudentService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaApplicationListener {

    private final StudentService studentService;

    private final ApplicationDtoMapper applicationDtoMapper;

    @KafkaListener(topics = "team-selection.applications.accepted")
    @RetryableTopic(attempts = "3", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    public void listenApplicationAccepted(ApplicationDto applicationDto) {
        Application application = applicationDtoMapper.mapToEntity(applicationDto);
        User targetUser = studentService.findByIdOrElseThrow(application.getTargetId()).getUser();
        log.info("Accept applications.accepted message. Sending email to {}", targetUser.getEmail());
    }

    @KafkaListener(topics = "team-selection.applications.rejected")
    @RetryableTopic(attempts = "3", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    public void listenApplicationRejected(ApplicationDto applicationDto) {
        Application application = applicationDtoMapper.mapToEntity(applicationDto);
        User targetUser = studentService.findByIdOrElseThrow(application.getTargetId()).getUser();
        log.info("Accept applications.rejected message. Sending email to {}", targetUser.getEmail());
    }
}
