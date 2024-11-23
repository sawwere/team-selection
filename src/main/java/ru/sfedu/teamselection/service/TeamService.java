package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.TeamCreationDto;
import ru.sfedu.teamselection.dto.TeamDto;
import ru.sfedu.teamselection.dto.TeamSearchOptionsDto;
import ru.sfedu.teamselection.mapper.TeamDtoMapper;
import ru.sfedu.teamselection.mapper.TechnologyDtoMapper;
import ru.sfedu.teamselection.repository.TeamRepository;
import ru.sfedu.teamselection.repository.specification.TeamSpecification;


@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    private final TeamDtoMapper teamDtoMapper;

    private final StudentService studentService;

    private final TechnologyDtoMapper technologyDtoMapper;

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
    @Transactional
    public Team create(TeamCreationDto teamDto) {
        Team team = teamDtoMapper.mapCreationToEntity(teamDto);
        String name = teamDto.getName();
        Long trackId = teamDto.getCurrentTrackId();
        if (teamRepository.existsByNameIgnoreCaseAndCurrentTrackId(name, trackId)) {
            team = teamRepository.findByNameIgnoreCaseAndCurrentTrackId(name, trackId).orElseThrow();
            //TODO: update fields
        } else {
            Student captain = studentService.findByIdOrElseThrow(teamDto.getCaptainId());
            team = addStudentToTeam(team, captain);
            team.setCaptainId(captain.getId());
            captain.setHasTeam(true);
            captain.setIsCaptain(true);
        }
        teamRepository.save(team);
        return team;
    }

    /**
     * Delete team
     * @param id team id
     */
    public void delete(Long id) {
        teamRepository.deleteById(id);
    }

    @Transactional
    public Team addStudentToTeam(Team team, Student student) {
        if (student.getHasTeam()) {
            throw new RuntimeException("Student already has team");
        }
        if (team.getIsFull()) {
            throw new RuntimeException("Cannot add student to full team");
        }
        if (student.getCourse() == 2) {
            long secondYearCount = team.getStudents().stream().filter(x -> x.getCourse() == 2).count();
            if (secondYearCount == team.getCurrentTrack().getMaxSecondCourseConstraint()) {
                throw new RuntimeException("Cannot add second year student to the team");
            }
        }
        if (team.getStudents().contains(student)) {
            throw new RuntimeException("Student is already in the team");
        }
        //All checks are ok, now we can add student
        team.getStudents().add(student);
        team.setQuantityOfStudents(team.getQuantityOfStudents() + 1);
        team.setIsFull(team.getQuantityOfStudents() == team.getCurrentTrack().getMaxConstraint());

        student.setHasTeam(true);
        student.setCurrentTeam(team);
        return team;
    }

    public Team addStudentToTeam(Long teamId, Long studentId) {
        Team team = findByIdOrElseThrow(teamId);
        Student student = studentService.findByIdOrElseThrow(studentId);
        addStudentToTeam(team, student);

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

    /**
     * Performs search across all students with given filter criteria
     * @param like like parameter for the student string representation
     * @param trackId team is assigned to this track
     * @param isFull is team full of students
     * @param projectType project type defined by team's captain
     * @param technologies student's technologies(skills)
     * @return the filtered list
     */
    public List<Team> search(String like,
                             Long trackId,
                             Boolean isFull,
                             String projectType,
                             List<Long> technologies) {
        Specification<Team> specification = Specification.allOf();
        if (like != null) {
            specification = specification.and(TeamSpecification.like(like));
        }
        if (trackId != null) {
            specification = specification.and(TeamSpecification.byTrack(trackId));
        }
        if (isFull != null) {
            specification = specification.and(TeamSpecification.byIsFull(isFull));
        }
        if (projectType != null) {
            specification = specification.and(TeamSpecification.byProjectType(projectType));
        }
        specification = specification.and(TeamSpecification.byTechnologies(technologies));

        return teamRepository.findAll(specification);
    }

    public int getSecondYearsCount(Team team) {
        int res = 0;
        for (Student student: team.getStudents()) {
            if (student.getCourse() == 2) {
                res += 1;
            }
        }
        return res;
    }

    public TeamSearchOptionsDto getSearchOptionsTeams(Long trackId) {
        var teams = search(null, trackId, null, null, null);
        TeamSearchOptionsDto teamSearchOptionsDto = new TeamSearchOptionsDto();
        for (Team team : teams) {
            teamSearchOptionsDto.getProjectTypes().add(team.getProjectType());
            teamSearchOptionsDto.getTechnologies().addAll(
                    team.getTechnologies()
                            .stream()
                            .map(technologyDtoMapper::mapToDto)
                            .toList()
            );
        }
        return teamSearchOptionsDto;
    }
}
