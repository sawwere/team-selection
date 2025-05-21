package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.enums.TrackType;

import java.util.Optional;

@Transactional
public interface TrackRepository extends JpaRepository<Track, Long> {
//    List<Track> findAllByType(String type);

    Optional<Track> findByNameIgnoreCaseAndType(String name, TrackType type);
}

