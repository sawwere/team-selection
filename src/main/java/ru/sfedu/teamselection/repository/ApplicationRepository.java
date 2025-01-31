package ru.sfedu.teamselection.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sfedu.teamselection.domain.application.Application;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    boolean existsByTeamIdAndStudentId(Long teamId, Long studentId);

    Application findByTeamIdAndStudentId(Long teamId, Long studentId);

}
