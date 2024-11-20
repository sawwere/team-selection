package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sfedu.teamselection.domain.Team;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByNameIgnoreCaseAndCurrentTrackId(String name, Long trackId);

    Optional<Team> findByNameIgnoreCaseAndCurrentTrackId(String name, Long trackId);
}

