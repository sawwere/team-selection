package ru.sfedu.teamselection.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sfedu.teamselection.domain.Student;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Student findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Student s
        SET s.isCaptain = false, s.currentTeam = null, hasTeam = false
        WHERE s.currentTrack IS NOT NULL
        AND s.currentTrack.endDate < :currentDate
    """)
    int deactivateCaptainsWithExpiredTracks(LocalDate currentDate);
}
