package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sfedu.teamselection.domain.audit.AuditEntity;

public interface AuditRepository  extends JpaRepository<AuditEntity, Long> {
}
