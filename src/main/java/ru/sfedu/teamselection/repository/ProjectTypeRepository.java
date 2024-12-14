package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sfedu.teamselection.domain.ProjectType;

public interface ProjectTypeRepository extends JpaRepository<ProjectType, Long> {
}
