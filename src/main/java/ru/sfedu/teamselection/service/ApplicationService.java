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
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.mapper.application.ApplicationCreationDtoMapper;
import ru.sfedu.teamselection.repository.ApplicationRepository;


@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final StudentService studentService;

    private final TeamService teamService;

    private final ApplicationCreationDtoMapper applicationCreationDtoMapper;


    public Application findByIdOrElseThrow(Long id) throws NoSuchElementException {
        return applicationRepository.findById(id).orElseThrow();
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    /**
     * Deletes application by id
     * @param id id of the application
     * @deprecated for future removal
     */
    public void delete(Long id) {
        applicationRepository.deleteById(id);
    }

    /**
     *
     * @param teamId
     * @return
     */
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

    private Application tryCreateApplication(ApplicationCreationDto dto, User sender)
            throws  ConstraintViolationException {
        Student student = studentService.findByIdOrElseThrow(dto.getStudentId());
        // Check that there is actually sender's id in the dto
        if (!sender.getId().equals(student.getUser().getId())) {
            throw new AccessDeniedException("Tried to create application for another user!");
        }

        // Can't apply if student already has team
        if (student.getHasTeam() || student.getIsCaptain()) {
            throw new ConstraintViolationException("Students with team cannot create applications");
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
        Application application = applicationCreationDtoMapper.mapToEntity(dto);
        application.setStatus(ApplicationStatus.Sent.toString().toLowerCase());
        return applicationRepository.save(application);
    }

    private boolean checkSenderIsCaptain(Team team, User sender) {
        Student actualCaptain = studentService.findByIdOrElseThrow(team.getCaptainId());
        return actualCaptain.getUser().getId().equals(sender.getId());
    }

    @Transactional
    private Application tryAcceptApplication(ApplicationCreationDto dto, User sender)
            throws ConstraintViolationException {
        Application application = findByIdOrElseThrow(dto.getId());
        Student student = studentService.findByIdOrElseThrow(application.getStudent().getId());

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
            throw new AccessDeniedException("Only captain of the team can accept application");
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

        application.setStatus(ApplicationStatus.Accepted.toString().toLowerCase());
        // TODO mark other applications as cancelled

        return applicationRepository.save(application);
    }

    @Transactional
    private Application tryRejectApplication(ApplicationCreationDto dto, User sender)
            throws ConstraintViolationException {
        Application application = findByIdOrElseThrow(dto.getId());
        Team team = teamService.findByIdOrElseThrow(application.getTeam().getId());
        // Check if sender is captain of the team.
        // Only captain can reject application
        if (!checkSenderIsCaptain(team, sender)) {
            throw new AccessDeniedException("Only captain of the team can reject application");
        }

        application.setStatus(ApplicationStatus.Rejected.toString().toLowerCase());
        return applicationRepository.save(application);
    }

    @Transactional
    private Application trCancelApplication(ApplicationCreationDto dto, User sender)
            throws ConstraintViolationException {
        Application application = findByIdOrElseThrow(dto.getId());
        // Check if sender is the sender of the application.
        if (!application.getStudent().getUser().getId().equals(sender.getId())) {
            throw new AccessDeniedException("Only sender of the application can reject application");
        }
        if (!application.getStatus().equalsIgnoreCase(ApplicationStatus.Sent.toString())) {
            throw new ConstraintViolationException("Can not cancel application with the status other than SENT");
        }


        application.setStatus(ApplicationStatus.Cancelled.toString().toLowerCase());
        return applicationRepository.save(application);
    }

    /**
     * Updates application entity status based in dto
     * @param dto containing info about application
     * @return updated entity
     * @throws NoSuchElementException in case there is no such application
     */
    @Transactional
    //TODO
    public Application update(ApplicationCreationDto dto, User sender) {
        Application application = findByIdOrElseThrow(dto.getId());
        if (application.getStatus().equals(ApplicationStatus.Accepted.toString().toLowerCase())) {
            throw new ConstraintViolationException("Can not update Accepted applications");
        }

        return switch (dto.getStatus().toLowerCase()) {
            case "accepted" -> tryAcceptApplication(dto, sender);
            case "rejected" -> tryRejectApplication(dto, sender);
            case "cancelled" -> trCancelApplication(dto, sender);
            default -> throw new RuntimeException("Unexpected application status '%s'".formatted(dto.getStatus()));
        };
    }

    @Transactional
    public Application create(ApplicationCreationDto dto, User sender) {
        if (dto.getId() == null) {
            return tryCreateApplication(dto, sender);
        } else if (applicationRepository.existsById(dto.getId())) {
            return update(dto, sender);
        } else {
            throw new NoSuchElementException("There is no such application to be updated");
        }
    }

    /**
     * Returns applications of the given pair of student and team
     * @param teamId id of the team
     * @param studentId id of the student
     * @return new list
     * @deprecated no usages, should be removed in the future
     */
    public Application findApplicationByTeamIdAndStudentId(long teamId, long studentId) {
        return applicationRepository.findByTeamIdAndStudentId(teamId, studentId);
    }
}
