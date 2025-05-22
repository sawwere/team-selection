package ru.sfedu.teamselection.service.scheduler;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.config.SchedulerConfig;
import ru.sfedu.teamselection.repository.StudentRepository;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(SchedulerConfig.class)
@Service
public class StudentUpdateScheduler {
    private final StudentRepository studentRepository;

    @Scheduled(cron = "${app.scheduler.studentTrack}")
    @Transactional
    void deactivateExpiredCaptains() {
        log.info("StudentUpdateScheduler: Start deactivateExpiredCaptains job");
        LocalDate today = LocalDate.now();
        var updatedCount = studentRepository.deactivateCaptainsWithExpiredTracks(today);
        log.info("StudentUpdateScheduler: Finish deactivateExpiredCaptains job, updated {} students", updatedCount);
    }
}
