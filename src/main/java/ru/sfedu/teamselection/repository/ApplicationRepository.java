package ru.sfedu.teamselection.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sfedu.teamselection.domain.Application;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    boolean existsByTeamIdAndStudentId(Long teamId, Long studentId);

    Application findByTeamIdAndStudentId(Long teamId, Long studentId);
}
