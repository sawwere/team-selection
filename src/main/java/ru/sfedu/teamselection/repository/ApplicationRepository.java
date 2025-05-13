package ru.sfedu.teamselection.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.application.Application;

import java.util.List;
import java.util.Optional;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    boolean existsByTeamIdAndStudentId(Long teamId, Long studentId);

    List<Application> findByTeamId(Long teamId);

    Optional<Application> findByTeamIdAndStudentId(Long teamId, Long studentId);

    @Transactional
    @Modifying
    @Query("update Application a set a.status = ?1 where a.team = ?2 and a.status = 'sent'")
    void updateStatusByTeam(String status, Team team);

    @Transactional
    @Modifying
    @Query("update Application a set a.status = ?1 where a.student = ?2 and a.status = 'sent'")
    void updateStatusByStudent(String status, Student student);


}
