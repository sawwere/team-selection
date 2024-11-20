package ru.sfedu.teamselection.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.ApplicationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.mapper.ApplicationDtoMapper;
import ru.sfedu.teamselection.repository.ApplicationRepository;


@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final StudentService studentService;

    private final TeamService teamService;

    private final ApplicationDtoMapper applicationDtoMapper;


    public Application findByIdOrElseThrow(Long id) throws NoSuchElementException {
        return applicationRepository.findById(id).orElseThrow();
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    public void delete(Long id) {
        applicationRepository.deleteById(id);
    }

    /**
     * Returns a list of teams for which student is subscribed
     * @param id student id
     * @return the new list
     */
    public List<Team> getUserApplications(Long id) {
        Student student = studentService.findByIdOrElseThrow(id);
        if (student.getApplications().isEmpty()) {
            return new ArrayList<>();
        } else {
            return student.getApplications().stream()
                    .map(application ->
                            teamService.findByIdOrElseThrow(application.getTeam().getId())
                    )
                    .toList();
        }
    }


    public List<Student> findTeamApplications(Long teamId) {
        Team team = teamService.findByIdOrElseThrow(teamId);
        if (team.getApplications().isEmpty()) {
            return new ArrayList<>();
        } else {
            return team.getApplications().stream()
                    .map(application -> studentService.findByIdOrElseThrow(application.getStudent().getId()))
                    .toList();
        }
    }

    private Application tryCreateApplication(ApplicationDto dto, User sender) throws  ConstraintViolationException {
        Student student = studentService.findByIdOrElseThrow(dto.getStudentId());
        // Check that there is actually sender's id in the dto
        if (sender.getId().equals(student.getUser().getId())) {
            throw new AccessDeniedException("Tried to create application for another user!");
        }

        // Can't apply if student already has team or is captain
        if (student.getHasTeam() || student.getIsCaptain()) {
            throw new ConstraintViolationException("Captains and students with team cannot create applications");
        }
        Team team = teamService.findByIdOrElseThrow(dto.getTeamId());
        // Check if team is full
        if (team.getIsFull()) {
            throw new ConstraintViolationException("Cannot apply for full team");
        }
        // Check if team already has maximum second year students
        if (student.getCourse() == 2
                && teamService.getSecondYearsCount(team) == team.getCurrentTrack().getMaxSecondCourseConstraint()) {
            throw new ConstraintViolationException("Cannot apply, team already has maximum second year students");
        }
        // Check if student is trying to join team from another track
        if (!studentService.typeOfStudentTrack(student).equals(team.getCurrentTrack().getType())) {
            throw new ConstraintViolationException("Cannot apply, wrong track");
        }

        // Creating new application with default status
        Application application = applicationDtoMapper.mapToEntity(dto);
        application.setStatus(ApplicationStatus.Sent.name());
        return applicationRepository.save(application);
    }

    private boolean checkSenderIsCaptain(Team team, User sender) {
        Student actualCaptain = studentService.findByIdOrElseThrow(team.getCaptainId());
        return actualCaptain.getUser().getId().equals(sender.getId());
    }

    @Transactional
    private Application tryAcceptApplication(ApplicationDto dto, User sender) throws ConstraintViolationException {
        Student student = studentService.findByIdOrElseThrow(dto.getStudentId());

        // Can't apply if student already has team or is captain
        if (student.getHasTeam()) {
            throw new ConstraintViolationException("Student %d already has team"
                    .formatted(student.getId())
            );
        }
        Team team = teamService.findByIdOrElseThrow(dto.getTeamId());
        // Check if sender is captain of the team.
        // Only captain can accept application
        if (!checkSenderIsCaptain(team, sender)) {
            throw new ConstraintViolationException("Only captain of the team can accept application");
        }
        // Check if team is full
        if (team.getIsFull()) {
            throw new ConstraintViolationException("Can't apply because team is already full");
        }
        // Check if team already has maximum second year students
        if (student.getCourse() == 2
                && teamService.getSecondYearsCount(team) == team.getCurrentTrack().getMaxSecondCourseConstraint()) {
            throw new ConstraintViolationException("Can't apply because team is already full of second year students");
        }
        teamService.addStudentToTeam(team, student);
        Application application = applicationRepository.findByTeamIdAndStudentId(dto.getTeamId(), dto.getStudentId());
        application.setStatus(ApplicationStatus.Accepted.name());
        // TODO mark other applications as cancelled

        return applicationRepository.save(application);
    }

    @Transactional
    private Application tryRejectApplication(ApplicationDto dto, User sender) throws ConstraintViolationException {
        Student student = studentService.findByIdOrElseThrow(dto.getStudentId());

        Team team = teamService.findByIdOrElseThrow(dto.getTeamId());
        // Check if sender is captain of the team.
        // Only captain can reject application
        if (!checkSenderIsCaptain(team, sender)) {
            throw new ConstraintViolationException("Only captain of the team can reject application");
        }
        teamService.addStudentToTeam(team, student);
        Application application = applicationRepository.findByTeamIdAndStudentId(dto.getTeamId(), dto.getStudentId());
        application.setStatus(ApplicationStatus.Rejected.name());
        return applicationRepository.save(application);
    }

    /**
     * Updates application entity status based in dto
     * @param dto containing info about application
     * @return updated entity
     * @throws NoSuchElementException in case there is no such application
     */
    @Transactional
    public Application update(ApplicationDto dto, User sender) throws NoSuchElementException {
        Application application = applicationRepository.findByTeamIdAndStudentId(dto.getTeamId(), dto.getStudentId());
        if (application == null) {
            throw new NoSuchElementException("There is no application with such data");
        }
        if (application.getStatus().equals(ApplicationStatus.Accepted.name().toLowerCase())) {
            throw new RuntimeException("Can update only applications in status 'Sent'");
        }
        switch (dto.getStatus().toLowerCase()) {
            case "accepted": {
                return tryAcceptApplication(dto, sender);
            }
            case "rejected": {
                return tryRejectApplication(dto, sender);
            }
            case "cancelled": {
                // TODO make security rules for application cancel
                application.setStatus(ApplicationStatus.Cancelled.name());
                break;
            }
            default: {
                throw new RuntimeException("Unexpected application status '%s'".formatted(dto.getStatus()));
            }
        }
        return applicationRepository.save(application);
    }

    @Transactional
    public Application create(ApplicationDto dto, User sender) {
        if (applicationRepository.existsByTeamIdAndStudentId(dto.getTeamId(), dto.getStudentId())) {
            return update(dto, sender);
        } else {
            return tryCreateApplication(dto, sender);
        }
    }
}
