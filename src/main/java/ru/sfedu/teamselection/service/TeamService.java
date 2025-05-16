package ru.sfedu.teamselection.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.student.StudentDto;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.dto.team.TeamSearchOptionsDto;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.ProjectTypeMapper;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.mapper.team.TeamCreationDtoMapper;
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

    private final TechnologyMapper technologyDtoMapper;
    private final ProjectTypeMapper projectTypeDtoMapper;
    private final TeamCreationDtoMapper teamCreationDtoMapper;

    /**
     * Find Team entity by id
     * @param id team id
     * @return entity with given id
     * @throws ru.sfedu.teamselection.exception.NotFoundException in case there is no team with such id
     */
    public Team findByIdOrElseThrow(Long id) throws NotFoundException {
        return teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team with id " + id + " not found"));
    }

    /**
     * Find all teams
     * @return list of teams
     */
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    /**
     * Performs search across all students with given filter criteria and pagination
     */
    public Page<Team> search(String like,
                             Long trackId,
                             Boolean isFull,
                             String projectType,
                             List<Long> technologies,
                             Pageable pageable) {
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

        return teamRepository.findAll(specification, pageable);
    }

    /**
     * Create new team or update existing team
     * @param dto TeamDto
     * @return the team
     */
    @Transactional
    public Team create(TeamCreationDto dto) {
        String name    = dto.getName();
        Long trackId   = dto.getCurrentTrackId();

        Team team;
        if (teamRepository.existsByNameIgnoreCaseAndCurrentTrackId(name, trackId)) {
            team = teamRepository
                    .findByNameIgnoreCaseAndCurrentTrackId(name, trackId)
                    .orElseThrow(() -> new NotFoundException(
                            "Existing team '" + name + "' on track " + trackId + " not found"
                    ));
            // просто обновляем поля
            team.setName(dto.getName());
            team.setProjectDescription(dto.getProjectDescription());
            team.setProjectType(projectTypeDtoMapper.mapToEntity(dto.getProjectType()));
        } else {
            // новая команда
            team = teamCreationDtoMapper.mapToEntity(dto);
            team.setCurrentTrack(trackService.findByIdOrElseThrow(trackId));

            Student captain = studentService.findByIdOrElseThrow(dto.getCaptainId());
            addStudentToTeam(team, captain, false);
            team.setCaptainId(captain.getId());
            captain.setHasTeam(true);
            captain.setIsCaptain(true);
        }

        // технологии
        team.setTechnologies(
                technologyRepository.findAllByIdIn(
                        dto.getTechnologies()
                                .stream()
                                .map(TechnologyDto::getId)
                                .toList()
                )
        );

        return teamRepository.save(team);
    }

    /**
     * Delete team
     * @param id team id
     */
    @Transactional
    public void delete(Long id) {
        Team team = findByIdOrElseThrow(id);
        for (Student teamMember : team.getStudents()) {
            if (Objects.equals(teamMember.getCurrentTeam().getId(), id)) {
                teamMember.setHasTeam(false);
                teamMember.setCurrentTeam(null);
                teamMember.setIsCaptain(false);
            }
            teamRepository.deleteById(id);
        }
    }

    @Transactional
    public Team addStudentToTeam(Team team, Student student, Boolean skipRestrictions) {
        if (student.getHasTeam()) {
            throw new ConstraintViolationException("Student already has a team");
        }
        if (!skipRestrictions && team.getIsFull()) {
            throw new ConstraintViolationException("Cannot add student to a full team");
        }
        // ограничение по второму курсу
        if (student.getCourse() == 2) {
            long count2 = team.getStudents().stream()
                    .filter(s -> s.getCourse() == 2)
                    .count();
            int max2 = team.getCurrentTrack().getMaxSecondCourseConstraint();
            if (count2 >= max2) {
                throw new ConstraintViolationException(
                        "Team already has maximum of " + max2 + " second-year students");
            }
        }
        // не дублируем участника
        if (team.getStudents().stream()
                .anyMatch(s -> s.getId().equals(student.getId()))) {
            throw new ConstraintViolationException("Student is already in the team");
        }

        // добавляем
        team.getStudents().add(student);
        team.setQuantityOfStudents(team.getQuantityOfStudents() + 1);
        team.setIsFull(
                team.getQuantityOfStudents().equals(
                        team.getCurrentTrack().getMaxConstraint()
                )
        );

        student.setHasTeam(true);
        student.setCurrentTeam(team);
        return team;
    }

    @Transactional
    public Team removeStudentFromTeam(Team team, Student student) {
        if (team.getCaptainId().equals(student.getId())) {
            throw new ConstraintViolationException("Cannot remove captain from their own team");
        }
        team.getStudents().removeIf(s -> s.getId().equals(student.getId()));
        team.setQuantityOfStudents(team.getQuantityOfStudents() - 1);
        team.setIsFull(false);

        student.setHasTeam(false);
        student.setCurrentTeam(null);
        return team;
    }

    /**
     *
     * @param teamId
     * @param studentId
     * @param sender
     * @return
     */
    @Transactional
    public Team addStudentToTeam(Long teamId, Long studentId, User sender) {
        Team team = findByIdOrElseThrow(teamId);
        Student student = studentService.findByIdOrElseThrow(studentId);
        addStudentToTeam(team, student, isAdmin(sender));

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
    @Transactional
    public Team update(Long id, TeamDto dto, User sender) {
        Team team = findByIdOrElseThrow(id);

        boolean isAdmin = isAdmin(sender);
        boolean isCaptainOfThis = sender.getId()
                .equals(studentService.findByIdOrElseThrow(team.getCaptainId()).getUser().getId());

        if (!isAdmin && !isCaptainOfThis) {
            throw new ForbiddenException("Only admin or the team’s captain can modify this team");
        }


        if (isAdmin) {
            team.setName(dto.getName());
            team.setQuantityOfStudents(dto.getQuantityOfStudents());
            team.setIsFull(dto.getIsFull());
            if (!Objects.equals(dto.getCurrentTrackId(), team.getCurrentTrack().getId())) {
                team.setCurrentTrack(trackService.findByIdOrElseThrow(dto.getCurrentTrackId()));
            }
            team.setCaptainId(dto.getCaptain().getId());
        }

        team.setProjectDescription(dto.getProjectDescription());
        team.setProjectType(projectTypeDtoMapper.mapToEntity(dto.getProjectType()));
        team.setTechnologies(
                technologyRepository.findAllByIdIn(
                        dto.getTechnologies().stream().map(TechnologyDto::getId).toList()
                )
        );


        List<Long> newStudentIds = dto.getStudents().stream()
                .map(StudentDto::getId)
                .toList();
        Set<Long> currentIds = team.getStudents().stream()
                .map(Student::getId)
                .collect(Collectors.toSet());

        for (Student s : new ArrayList<>(team.getStudents())) {
            if (!newStudentIds.contains(s.getId())) {

                removeStudentFromTeam(team, s);
            }
        }

        for (Long sid : newStudentIds) {
            if (!currentIds.contains(sid)) {
                Student student = studentService.findByIdOrElseThrow(sid);
                addStudentToTeam(team, student, isAdmin);
            }
        }

        return teamRepository.save(team);
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

    private boolean isAdmin(User user) {
        return user.getRole().getName().equals("ADMIN");
    }
}
