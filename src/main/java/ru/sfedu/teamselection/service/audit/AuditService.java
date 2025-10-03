package ru.sfedu.teamselection.service.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.config.logging.AuditableInterceptor;
import ru.sfedu.teamselection.domain.audit.AuditEntity;
import ru.sfedu.teamselection.repository.AuditRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuditService {
    private final AuditRepository auditRepository;

    @Transactional
    public void log(
            AuditableInterceptor.AuditDetails auditDetails,
            String auditPoint,
            String remoteAddress,
            String payload
    ) {
        log.info(remoteAddress);
        var jsonPayload = payload.isBlank() ? null : payload;
        auditRepository.save(
                AuditEntity.builder().auditPoint(auditPoint)
                .senderEmail(auditDetails.userEmail())
                .payload(jsonPayload)
                .build()
        );
    }
}
