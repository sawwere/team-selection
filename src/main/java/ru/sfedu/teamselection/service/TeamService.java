package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.dto.team.TeamSearchOptionsDto;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.ProjectTypeDtoMapper;
import ru.sfedu.teamselection.mapper.TechnologyDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamCreationDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamDtoMapper;
import ru.sfedu.teamselection.repository.ProjectTypeRepository;
import ru.sfedu.teamselection.repository.TeamRepository;
import ru.sfedu.teamselection.repository.TechnologyRepository;
import ru.sfedu.teamselection.repository.specification.TeamSpecification;


@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final TechnologyRepository technologyRepository;
    private final ProjectTypeRepository projectTypeRepository;

    private final TrackService trackService;
    private final StudentService studentService;

    private final TechnologyDtoMapper technologyDtoMapper;
    private final ProjectTypeDtoMapper projectTypeDtoMapper;
    private final TeamCreationDtoMapper teamCreationDtoMapper;

    /**
     * Find Team entity by id
     * @param id team id
     * @return entity with given id
     * @throws ru.sfedu.teamselection.exception.NotFoundException in case there is no team with such id
     */
    public Team findByIdOrElseThrow(Long id) throws NotFoundException {
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
        Team team = teamCreationDtoMapper.mapToEntity(teamDto);
        String name = teamDto.getName();
        Long trackId = teamDto.getCurrentTrackId();
        if (teamRepository.existsByNameIgnoreCaseAndCurrentTrackId(name, trackId)) {
            team = teamRepository.findByNameIgnoreCaseAndCurrentTrackId(name, trackId).orElseThrow();
            team.setName(teamDto.getName());
            team.setProjectDescription(teamDto.getProjectDescription());
            team.setProjectType(projectTypeDtoMapper.mapToEntity(teamDto.getProjectType()));
        } else {
            team.setCurrentTrack(trackService.findByIdOrElseThrow(teamDto.getCurrentTrackId()));
            Student captain = studentService.findByIdOrElseThrow(teamDto.getCaptainId());

            team = addStudentToTeam(team, captain);
            team.setCaptainId(captain.getId());
            captain.setHasTeam(true);
            captain.setIsCaptain(true);
        }

        team.setTechnologies(technologyRepository.findAllByIdIn(
                teamDto.getTechnologies()
                        .stream()
                        .map(TechnologyDto::getId)
                        .toList())
        );
        teamRepository.save(team);
        return team;
    }

    /**
     * Delete team
     * @param id team id
     */
    @Transactional
    public void delete(Long id) {
        for (Student teamMember: findByIdOrElseThrow(id).getStudents()) {
            // there is no need to clean up information about the current team
            // if the student WAS a member of it earlier, but now belongs to ANOTHER one.
            if (teamMember.getCurrentTeam().getId().equals(id)) {
                teamMember.setHasTeam(false);
                teamMember.setCurrentTeam(null);
                teamMember.setIsCaptain(false);
            }
        }
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
        if (team.getStudents().stream().anyMatch(x -> x.getId().equals(student.getId()))) {
            throw new RuntimeException("Student is already in the team");
        }
        //All checks are ok, now we can add student
        team.getStudents().add(student);
        team.setQuantityOfStudents(team.getQuantityOfStudents() + 1);
        team.setIsFull(Objects.equals(team.getQuantityOfStudents(), team.getCurrentTrack().getMaxConstraint()));

        student.setHasTeam(true);
        student.setCurrentTeam(team);
        return team;
    }

    @Transactional
    public Team removeStudentFromTeam(Team team, Student student) {
        if (team.getCaptainId().equals(student.getId())) {
            throw new ConstraintViolationException("Can't remove captain from their team.");
        }
        team.getStudents().removeIf(x -> x.getId().equals(student.getId()));
        team.setQuantityOfStudents(team.getQuantityOfStudents() - 1);
        // If the team was full, it isn't now because 1 member has been removed,
        // otherwise the property is not changed.
        team.setIsFull(false);

        student.setCurrentTeam(null);
        student.setHasTeam(false);
        return team;
    }

    @Transactional
    public Team addStudentToTeam(Long teamId, Long studentId) {
        Team team = findByIdOrElseThrow(teamId);
        Student student = studentService.findByIdOrElseThrow(studentId);
        addStudentToTeam(team, student);

        return team;
    }

    /**
     * Updates entity using data given in dto
     * # WARNING: unsafe method. No business-logic validation is performed here depending on sender authorities.
     * @param id id of entity
     * @param dto dto containing updated values
     * @return updated entity
     * @apiNote   possibly UNSAFE
     */
    public Team update(Long id, TeamDto dto, User sender) {
        boolean isUnsafeAllowed = false;
        Team team = findByIdOrElseThrow(id);
        if (sender.getRole().getName().equals("ADMIN")) {
            isUnsafeAllowed = true;
        } else if (!sender.getId().equals(studentService.findByIdOrElseThrow(team.getCaptainId()).getUser().getId())) {
            throw new ForbiddenException("Team info can be modified only by its captain");
        }

        if (isUnsafeAllowed) {
            team.setName(dto.getName());
            team.setQuantityOfStudents(dto.getQuantityOfStudents());
            team.setIsFull(dto.getIsFull());
            // do only if not equals thus saving database calls
            if (!Objects.equals(dto.getCurrentTrackId(), team.getCurrentTrack().getId())) {
                team.setCurrentTrack(trackService.findByIdOrElseThrow(dto.getCurrentTrackId()));
            }
            team.setCaptainId(dto.getCaptain().getId());
        }

        team.setProjectDescription(dto.getProjectDescription());
        team.setProjectType(projectTypeDtoMapper.mapToEntity(dto.getProjectType()));
        team.setTechnologies(technologyRepository.findAllByIdIn(
                dto.getTechnologies()
                        .stream()
                        .map(TechnologyDto::getId)
                        .toList())
        );


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

    @Transactional(readOnly = true)
    public TeamSearchOptionsDto getSearchOptionsTeams(Long trackId) {
        var teams = search(null, trackId, null, null, null);
        TeamSearchOptionsDto teamSearchOptionsDto = new TeamSearchOptionsDto();
        teamSearchOptionsDto
                .getProjectTypes()
                .addAll(projectTypeDtoMapper.mapListToDto(projectTypeRepository.findAll()));
        for (Team team : teams) {
            teamSearchOptionsDto.getTechnologies().addAll(
                    team.getTechnologies()
                            .stream()
                            .map(technologyDtoMapper::mapToDto)
                            .toList()
            );
        }
        return teamSearchOptionsDto;
    }

    public List<Team> getTeamHistoryForStudent(Long studentId) {
        return teamRepository.findAllByStudent(studentId);
    }
}
