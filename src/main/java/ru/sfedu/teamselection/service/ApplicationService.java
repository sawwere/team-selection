package ru.sfedu.teamselection.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.dto.application.ApplicationResponseDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.BusinessException;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.application.ApplicationMapper;
import ru.sfedu.teamselection.repository.ApplicationRepository;
import ru.sfedu.teamselection.repository.specification.ApplicationSpecification;
import ru.sfedu.teamselection.service.validation.ApplicationValidator;
import ru.sfedu.teamselection.service.validation.ValidationResult;
import static ru.sfedu.teamselection.enums.ApplicationStatus.ACCEPTED;
import static ru.sfedu.teamselection.enums.ApplicationStatus.CANCELLED;
import static ru.sfedu.teamselection.enums.ApplicationStatus.REJECTED;
import static ru.sfedu.teamselection.enums.ApplicationStatus.SENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    private static final Logger LOGGER = Logger.getLogger(ApplicationService.class.getName());

    private final ApplicationRepository applicationRepository;

    private final StudentService studentService;

    private final TeamService teamService;

    private final ApplicationMapper applicationMapper;

    private final ApplicationValidator applicationValidator;


    public Application findByIdOrElseThrow(Long id) throws NotFoundException {
        return applicationRepository.findById(id).orElseThrow();
    }

    public Page<Application> findAll(Long trackId, String status, Pageable pageable) {

        Specification<Application> spec = (root, query, cb) -> cb.conjunction();

        if (trackId != null) {
            spec = spec.and(ApplicationSpecification.byTrack(trackId));
        }
        if (status != null) {
            spec = spec.and(ApplicationSpecification.byStatus(status));
        }

        Sort sort = pageable.getSort();
        for (Sort.Order order : sort) {
            if ("name".equals(order.getProperty())) {
                pageable = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(order.getDirection(), "student.fio")
                );
                break;
            }
        }
        return applicationRepository.findAll(spec, pageable);
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
        ValidationResult validationResult = applicationValidator.validateUpdate(dto.getStatus(), sender, existing);
        if (validationResult instanceof ValidationResult.Failure) {
            throw new BusinessException(((ValidationResult.Failure) validationResult).message);
        } else if (validationResult instanceof ValidationResult.Forbidden) {
            throw new ForbiddenException(((ValidationResult.Forbidden) validationResult).message);
        }

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
        ValidationResult validationResult = applicationValidator.validateCreate(dto, sender, false);
        if (validationResult instanceof ValidationResult.Failure) {
            throw new BusinessException(((ValidationResult.Failure) validationResult).message);
        } else if (validationResult instanceof ValidationResult.Forbidden) {
            throw new ForbiddenException(((ValidationResult.Forbidden) validationResult).message);
        }
        var app = applicationMapper.mapCreationToEntity(dto);
        app.setStatus(SENT.name());
        app.setStudent(studentService.findByIdOrElseThrow(dto.getStudentId()));
        app.setTeam(teamService.findByIdOrElseThrow(dto.getTeamId()));
        return applicationRepository.save(app);
    }

    /**
     * Finds application by teamId and studentId or throws NotFoundException.
     */
    public ApplicationResponseDto findByTeamAndStudentOrElseThrow(Long teamId, Long studentId, User currentUser) {
        Application application = applicationRepository.findByTeamIdAndStudentId(teamId, studentId).orElse(null);
        if (application == null) {
            return null;
        }

        ApplicationResponseDto result = applicationMapper.mapToResponseDto(application);
        result.setPossibleTransitions(new ArrayList<>());
        for (ApplicationStatus status : ApplicationStatus.values()) {
            if (applicationValidator.validateUpdate(status, currentUser, application)
                    instanceof ValidationResult.Success
            ) {
                result.getPossibleTransitions().add(status);
            }
        }
        return result;
    }

    private Application accept(Application app, User sender) {
        teamService.addStudentToTeam(app.getTeam(), app.getStudent(), false);
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
        app.setStatus(CANCELLED.name());
        return app;
    }

    private Application resend(Application app, User sender) {
        app.setStatus(SENT.name());
        return app;
    }

}
