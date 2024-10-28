package ru.sfedu.teamselection.service;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.repository.TeamRepository;


@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public Team findByIdOrElseThrow(Long id) throws NoSuchElementException {
        return teamRepository.findById(id).orElseThrow();
    }
}
