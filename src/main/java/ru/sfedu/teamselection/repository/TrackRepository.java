package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Track;

import java.util.List;

@Transactional
public interface TrackRepository extends JpaRepository<Track, Long> {
//    List<Track> findAllByType(String type);
}

