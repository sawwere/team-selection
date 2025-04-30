package ru.sfedu.teamselection.service;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.application.TeamRequest;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.application.ApplicationMapper;
import ru.sfedu.teamselection.repository.ApplicationRepository;


@Service
@RequiredArgsConstructor
public class ApplicationService {
    private static final Logger LOGGER = Logger.getLogger(ApplicationService.class.getName());

    private final EntityManager entityManager;
    private final ApplicationRepository applicationRepository;

    private final StudentService studentService;

    private final TeamService teamService;

    private final ApplicationMapper applicationMapper;


    public Application findByIdOrElseThrow(Long id) throws NotFoundException {
        return applicationRepository.findById(id).orElseThrow();
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    /**
     * Deletes application by id
     * @param id id of the application
     */
    public void delete(Long id) {
        applicationRepository.deleteById(id);
        LOGGER.info("Delete application(id=%s)".formatted(id));
    }

    /**
     * Returns student who applied for the team with the given id
     * @param teamId id of the team
     * @return new list
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
        // Creating new application with default status
        Application application = applicationMapper.mapToEntity(dto);
        application.setStatus(ApplicationStatus.SENT.toString());
        application.setStudent(entityManager.getReference(Student.class, dto.getStudentId()));
        application.setTeam(entityManager.getReference(Team.class, dto.getTeamId()));

        Student student = studentService.findByIdOrElseThrow(dto.getStudentId());
        // Check that there is actually sender's id in the dto
        if (!studentService.findByIdOrElseThrow(application.getSenderId()).getUser().getId()
                .equals(sender.getId())) {
            throw new ForbiddenException("Tried to create application for another user!");
        }

        validateStudentHasNoCurrentTeam(student);

        Team team = teamService.findByIdOrElseThrow(dto.getTeamId());
        // Check if team is full
        if (team.getIsFull()) {
            throw new ConstraintViolationException("Cannot apply for full team");
        }
        // Check if team already has maximum second year students
        validateIsNotOverLimitOfSecondYears(student, team);
        // Check if student is trying to join team from another track
        if (!studentService.typeOfStudentTrack(student).equals(team.getCurrentTrack().getType())) {
            throw new ConstraintViolationException("Cannot apply, wrong track");
        }

        return applicationRepository.save(application);
    }

    private void validateIsNotOverLimitOfSecondYears(Student student, Team team) {
        if (student.getCourse() == 2
                && teamService.getSecondYearsCount(team) == team.getCurrentTrack().getMaxSecondCourseConstraint()) {
            throw new ConstraintViolationException("Cannot apply, team already has maximum second year students");
        }
    }

    private void validateStudentHasNoCurrentTeam(Student student) {
        if (student.getHasTeam()) {
            throw new ConstraintViolationException("Student %d already has team"
                    .formatted(student.getId())
            );
        }
    }

    private void validateSenderIsTarget(Application application, User sender) {
        Student actualTarget = studentService.findByIdOrElseThrow(application.getTargetId());
        if (!actualTarget.getUser().getId().equals(sender.getId())) {
            throw new ForbiddenException();
        }
        // TODO возможно стоит сделать покрасивее?
        if (application instanceof TeamRequest) {
            if (actualTarget.getCurrentTeam() == null
                    || !actualTarget.getCurrentTeam().getId().equals(application.getTeam().getId())) {
                throw new ForbiddenException();
            }
        }
    }

    @Transactional
    private Application tryAcceptApplication(ApplicationCreationDto dto, User sender)
            throws ConstraintViolationException {
        Application application = findByIdOrElseThrow(dto.getId());
        Student student = studentService.findByIdOrElseThrow(application.getStudent().getId());

        validateStudentHasNoCurrentTeam(student);
        Team team = teamService.findByIdOrElseThrow(application.getTeam().getId());
        // Only target can accept application
        validateSenderIsTarget(application, sender);

        // Check if team is full
        if (team.getIsFull()) {
            throw new ConstraintViolationException("Can't apply because team is already full");
        }
        // Check if team already has maximum second year students
        validateIsNotOverLimitOfSecondYears(student, team);

        teamService.addStudentToTeam(team, student);

        application.setStatus(ApplicationStatus.ACCEPTED.toString());
        if (team.getIsFull()) {
            applicationRepository.updateStatusByTeam(ApplicationStatus.CANCELLED.toString(), team);
            applicationRepository.updateStatusByStudent(ApplicationStatus.CANCELLED.toString(), student);
        }

        return applicationRepository.save(application);
    }

    private Application tryResentApplication(ApplicationCreationDto dto, User sender)
            throws ConstraintViolationException {
        Application application = findByIdOrElseThrow(dto.getId());
        Student student = studentService.findByIdOrElseThrow(application.getStudent().getId());

        validateStudentHasNoCurrentTeam(student);
        Team team = teamService.findByIdOrElseThrow(application.getTeam().getId());


        if (team.getIsFull()) {
            throw new ConstraintViolationException("Cannot apply for full team");
        }

        validateIsNotOverLimitOfSecondYears(student, team);

        if (!studentService.typeOfStudentTrack(student).equals(team.getCurrentTrack().getType())) {
            throw new ConstraintViolationException("Cannot apply, wrong track");
        }
        application.setStatus(ApplicationStatus.SENT.toString());
        return applicationRepository.save(application);
    }

    @Transactional
    private Application tryRejectApplication(ApplicationCreationDto dto, User sender)
            throws ConstraintViolationException {
        Application application = findByIdOrElseThrow(dto.getId());

        // Only target can reject application
        validateSenderIsTarget(application, sender);

        application.setStatus(ApplicationStatus.REJECTED.toString());
        return applicationRepository.save(application);
    }

    @Transactional
    private Application trCancelApplication(ApplicationCreationDto dto, User sender)
            throws ConstraintViolationException {
        Application application = findByIdOrElseThrow(dto.getId());
        Student applicationSender = studentService.findByIdOrElseThrow(application.getSenderId());
        // Check if sender is the sender of the application.
        if (!applicationSender.getUser().getId().equals(sender.getId())) {
            throw new ForbiddenException("Only sender of the application can reject application");
        }

        application.setStatus(ApplicationStatus.CANCELLED.toString());
        return applicationRepository.save(application);
    }

    /**
     * Updates application entity status based in dto
     * @param dto containing info about application
     * @return updated entity
     * @throws NotFoundException in case there is no such application
     */
    @Transactional
    public Application update(ApplicationCreationDto dto, User sender) {
        Application application = findByIdOrElseThrow(dto.getId());


        return switch (dto.getStatus()) {
            case ACCEPTED -> tryAcceptApplication(dto, sender);
            case REJECTED -> tryRejectApplication(dto, sender);
            case CANCELLED -> trCancelApplication(dto, sender);
            case SENT -> tryResentApplication(dto, sender);
            default ->
                    throw new ConstraintViolationException(
                    "Unexpected application status '%s'".formatted(dto.getStatus())
            );
        };
    }

    @Transactional
    public Application create(ApplicationCreationDto dto, User sender) {
        if (dto.getId() == null) {
            return tryCreateApplication(dto, sender);
        } else if (applicationRepository.existsById(dto.getId())) {
            return update(dto, sender);
        } else {
            throw new NotFoundException("There is no such application to be updated");
        }
    }

    /**
     * Finds application by teamId and studentId or throws NotFoundException.
     */
    public Application findByTeamAndStudentOrElseThrow(Long teamId, Long studentId) throws NotFoundException {
        return applicationRepository.findByTeamIdAndStudentId(teamId, studentId)
                .orElse(null);
    }

}
