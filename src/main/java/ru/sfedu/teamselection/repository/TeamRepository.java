package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sfedu.teamselection.domain.Team;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

//    List<Team> findAllByFullFlagAndCurrentTrackId(boolean isFull, Long trackId);
//
//    List<Team> findTeamByTagsInAndCurrentTrackId(List<String> tags, Long trackId);
//
//    List<Team> findAllByCurrentTrackId(Long trackId);
}

