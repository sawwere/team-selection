package ru.sfedu.teamselection.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sfedu.teamselection.domain.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {

    boolean existsByNameIgnoreCaseAndCurrentTrackId(String name, Long trackId);

    Optional<Team> findByNameIgnoreCaseAndCurrentTrackId(String name, Long trackId);

    @Query("""
            select t
            from Team t
            left join t.students s
            where s.id = :studentId
""")
    List<Team> findAllByStudent(Long studentId);
}

