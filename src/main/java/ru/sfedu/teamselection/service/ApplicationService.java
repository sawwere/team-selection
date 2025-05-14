package ru.sfedu.teamselection.service;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.BusinessException;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.application.ApplicationMapper;
import ru.sfedu.teamselection.repository.ApplicationRepository;
import ru.sfedu.teamselection.service.validation.ApplicationValidator;

import static ru.sfedu.teamselection.enums.ApplicationStatus.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    private static final Logger LOGGER = Logger.getLogger(ApplicationService.class.getName());

    private final EntityManager entityManager;
    private final ApplicationRepository applicationRepository;

    private final StudentService studentService;

    private final TeamService teamService;

    private final ApplicationMapper applicationMapper;

    private final ApplicationValidator applicationValidator;


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
    public List<Student> findTeamApplicationsStudents(Long teamId) {
        Team team = teamService.findByIdOrElseThrow(teamId);
        if (team.getApplications().isEmpty()) {
            return new ArrayList<>();
        } else {
            return team.getApplications().stream()
                    .map(application -> studentService.findByIdOrElseThrow(application.getStudent().getId()))
                    .toList();
        }
    }

    //public List<ApplicationDto> findTeamApplications(Long teamId)
    //{
     //   return applicationRepository.findByTeamId(teamId).stream().map(x->applicationMapper.mapToDto(x)).toList();
  //  }



    /**
     * Updates application entity status based in dto
     * @param dto containing info about application
     * @return updated entity
     * @throws NotFoundException in case there is no such application
     */
    @Transactional
    public Application update(ApplicationCreationDto dto, User sender) {
        log.info("PUT /applications — update dto={} by user={}", dto, sender.getId());
        var existing = findByIdOrElseThrow(dto.getId());
        applicationValidator.validateUpdate(dto, sender, existing);


        switch (dto.getStatus()) {
            case ACCEPTED -> {
                return accept(existing, sender);
            }
            case REJECTED -> {
                return reject(existing, sender);
            }
            case CANCELLED -> {
                return cancel(existing, sender);
            }
            case SENT -> {
                return resend(existing, sender);
            }
            default -> throw new BusinessException("Статус не поддерживается: " + dto.getStatus());
        }
    }

    @Transactional
    public Application create(ApplicationCreationDto dto, User sender) {
        log.info("POST /applications — create dto={} by user={}", dto, sender.getId());
        if (dto.getId() != null && applicationRepository.existsById(dto.getId())) {
            return update(dto, sender);
        }
        applicationValidator.validateCreate(dto, sender);
        var app = applicationMapper.mapCreationToEntity(dto);
        app.setStatus(SENT.name());
        app.setStudent(studentService.findByIdOrElseThrow(dto.getStudentId()));
        app.setTeam(teamService.findByIdOrElseThrow(dto.getTeamId()));
        return applicationRepository.save(app);
    }

    /**
     * Finds application by teamId and studentId or throws NotFoundException.
     */
    public Application findByTeamAndStudentOrElseThrow(Long teamId, Long studentId) throws NotFoundException {
        return applicationRepository.findByTeamIdAndStudentId(teamId, studentId)
                .orElse(null);
    }

    private Application accept(Application app, User sender) {
        teamService.addStudentToTeam(app.getTeam(), app.getStudent());
        app.setStatus(ACCEPTED.name());
        applicationRepository.updateStatusByTeam(ApplicationStatus.CANCELLED.name(), app.getTeam());
        applicationRepository.updateStatusByStudent(ApplicationStatus.CANCELLED.name(), app.getStudent());
        return app;
    }

    private Application reject(Application app, User sender) {
        app.setStatus(REJECTED.name());
        return app;
    }

    private Application cancel(Application app, User sender) {
        app.setStatus(ApplicationStatus.CANCELLED.name());
        return app;
    }

    private Application resend(Application app, User sender) {
        app.setStatus(SENT.name());
        return app;
    }

}
