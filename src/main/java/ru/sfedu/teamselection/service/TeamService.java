package ru.sfedu.teamselection.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.dto.team.TeamSearchOptionsDto;
import ru.sfedu.teamselection.dto.team.TeamUpdateDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.ProjectTypeMapper;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.mapper.team.TeamCreationDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamUpdateDtoMapper;
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
    @Lazy
    @Autowired
    private StudentService studentService;

    private final TechnologyMapper technologyDtoMapper;
    private final ProjectTypeMapper projectTypeDtoMapper;
    private final TeamCreationDtoMapper teamCreationDtoMapper;
    private final TeamUpdateDtoMapper teamUpdateDtoMapper;

    @Autowired
    @Lazy
    private ApplicationService applicationService;

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
     * Performs search across all students with given filter criteria
     * @param like like parameter for the student string representation
     * @param trackId team is assigned to this track
     * @param isFull is team full of students
     * @param projectType project type defined by team's captain
     * @param technologies team's technologies(skills)
     * @param pageable pageable
     * @return the filtered list
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
        for (Application application: team.getApplications()) {
            applicationService.delete(application.getId());
        }
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
        for (Application application: student.getApplications()) {
            application.setStatus(ApplicationStatus.REJECTED.name());
        }
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
     * Добавляет студента в команду
     * @param teamId идентификатор команды, в которую будет добавлен студент
     * @param studentId идентификатор студента, который будет добавлен в команду
     * @param sender пользователь, инициирующий действие
     * @return обновленная команда
     */
    @Transactional
    public Team addStudentToTeam(Long teamId, Long studentId, User sender) {
        Team team = findByIdOrElseThrow(teamId);
        Student student = studentService.findByIdOrElseThrow(studentId);

        return addStudentToTeam(team, student, isAdmin(sender));
    }

    /**
     * Updates entity using data given in dto
     * # WARNING: unsafe method. No business-logic validation is performed here depending on sender authorities.
     * @param id id of entity
     * @return updated entity
     * @apiNote   possibly UNSAFE
     */
    @Transactional
    public Team update(Long id,
                       TeamUpdateDto dto,
                       User sender) {
        Team partial = teamUpdateDtoMapper.toEntity(dto);

        Team team = findByIdOrElseThrow(id);


        boolean isAdmin = isAdmin(sender);
        boolean isCaptain = sender.getId()
                .equals(studentService.findByIdOrElseThrow(team.getCaptainId()).getUser().getId());

        if (!isAdmin && !isCaptain) {
            throw new ForbiddenException("Only admin or captain");
        }

        // Только admin может менять эти поля:
        if (isAdmin) {
            team.setName(partial.getName());
            team.setIsFull(partial.getIsFull());
            if (!Objects.equals(partial.getCurrentTrack().getId(),
                    team.getCurrentTrack().getId())) {
                team.setCurrentTrack(
                        trackService.findByIdOrElseThrow(partial.getCurrentTrack().getId())
                );
            }
            team.setCaptainId(partial.getCaptainId());
        }

        // Всегда можно менять:
        team.setProjectDescription(partial.getProjectDescription());
        team.setProjectType(partial.getProjectType());
        team.setTechnologies(
                technologyRepository.findAllByIdIn(
                        partial.getTechnologies()
                                .stream()
                                .map(Technology::getId)
                                .collect(Collectors.toList())
                )
        );

        var newStudentIds = dto.getStudentIds();
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
        var teams = search(null, trackId, null, null, null, Pageable.unpaged());
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
