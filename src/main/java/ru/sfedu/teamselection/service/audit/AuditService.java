package ru.sfedu.teamselection.service.audit;

import java.util.UUID;
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
            UUID traceId,
            AuditableInterceptor.AuditDetails auditDetails,
            String auditPoint,
            String remoteAddress,
            String payload
    ) {
        var jsonPayload = payload.isBlank() ? null : payload;
        auditRepository.save(
                AuditEntity.builder()
                        .traceId(traceId)
                        .auditPoint(auditPoint)
                        .senderEmail(auditDetails.userEmail())
                        .remoteAddress(remoteAddress)
                        .payload(jsonPayload)
                        .build()
        );
    }
}
