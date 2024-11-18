package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.StudentDto;
import ru.sfedu.teamselection.dto.TeamDto;
import ru.sfedu.teamselection.mapper.TeamDtoMapper;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.TeamRepository;


@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    private final TeamDtoMapper teamDtoMapper;

    private final StudentRepository studentRepository;

    /**
     * Find Team entity by id
     * @param id team id
     * @return entity with given id
     * @throws NoSuchElementException in case there is no team with such id
     */
    public Team findByIdOrElseThrow(Long id) throws NoSuchElementException {
        return teamRepository.findById(id).orElseThrow();
    }

    /**
     * Find all teams
     * @return the list of all the teams
     */
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    /**
     * Create new team or update existing team
     * @param teamDto TeamDto
     * @return the team
     */
    public Team create(TeamDto teamDto)
    {
        Team team = teamDtoMapper.mapToEntity(teamDto);
        String name = teamDto.getName();
        Long trackId = teamDto.getCurrentTrackId();
        if (teamRepository.existsByNameIgnoreCaseAndCurrentTrackId(name,trackId)){
            team = teamRepository.findByNameIgnoreCaseAndCurrentTrackId(name, trackId).orElseThrow();
            //TODO: update fields
        }
        teamRepository.save(team);
        return team;
    }

    /**
     * Delete team
     * @param id team id
     */
    public void delete(Long id)
    {
        teamRepository.deleteById(id);
    }




    public Team addStudentToTeam(Long teamId, Long studentId)
    {
        Team team = findByIdOrElseThrow(teamId);
        Student student = studentRepository.findById(studentId).orElseThrow();
        if (!team.getStudents().contains(student))
        {
            team.getStudents().add(student);
        }

        return team;

    }

    public Team update(Long id, TeamDto dto) {
        Team team = findByIdOrElseThrow(id);

//        student.setFio(dto.getFio());
//        student.setEmail(dto.getEmail());
//        student.setCaptain(dto.getCaptain());
//        student.setStatus(dto.getStatus());
//        student.setAboutSelf(dto.getAboutSelf());
//        student.setCourse(dto.getCourse());
//        student.setContacts(dto.getContacts());
//        student.setGroupNumber(dto.getGroupNumber());
//        student.setTags(dto.getTags());

        teamRepository.save(team);
        return team;
    }
}
