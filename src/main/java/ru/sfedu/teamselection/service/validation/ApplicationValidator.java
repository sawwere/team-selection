package ru.sfedu.teamselection.service.validation;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.application.ApplicationType;
import ru.sfedu.teamselection.domain.application.TeamRequest;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.BusinessException;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.exception.ResourceNotFoundException;
import ru.sfedu.teamselection.service.StudentService;
import ru.sfedu.teamselection.service.TeamService;
import ru.sfedu.teamselection.service.UserService;

@Component
@RequiredArgsConstructor
public class ApplicationValidator {
    private final StudentService studentService;
    private final TeamService teamService;

    private final UserService userService;

    public void validateCreate(ApplicationCreationDto dto, User sender) {
        var team = teamService.findByIdOrElseThrow(dto.getTeamId());
        var captain = studentService.findByIdOrElseThrow(team.getCaptainId());
        var student = studentService.findByIdOrElseThrow(dto.getStudentId());

        var applicationSender =  dto.getType().equals(ApplicationType.INVITE)
                ? captain
                : student;
        if (!applicationSender.getUser().getId().equals(sender.getId())) {
            throw new ForbiddenException("Вы не можете подать заявку от имени другого студента");
        }
        if (student.getHasTeam()) {
            throw new BusinessException("Студент уже состоит в команде");
        }


        if (team.getIsFull()) {
            throw new BusinessException("Невозможно подать заявку — команда полная");
        }
        if (student.getCourse() == 2
                && teamService.getSecondYearsCount(team) >= team.getCurrentTrack().getMaxSecondCourseConstraint()) {
            throw new BusinessException("Невозможно — в команде уже максимальное число второкурсников");
        }
        if (!Objects.equals(student.getCurrentTrack().getId(), captain.getCurrentTrack().getId())) {
            throw new BusinessException("Невозможно — неверный трек");
        }
    }

    public void validateUpdate(ApplicationCreationDto dto, User requestSender, Application app) {
        if (app == null) {
            throw new ResourceNotFoundException("Application", dto.getId());
        }
        if (app.getStatus().toLowerCase().equals(ApplicationStatus.ACCEPTED.toString())) {
            throw new BusinessException("Невозможно изменить статус принятой заявки");
        }
        var sender = studentService.findByIdOrElseThrow(app.getSenderId());

        switch (dto.getStatus()) {
            case ACCEPTED -> {
                validateSenderIsTarget(app, requestSender);
                var team = app.getTeam();
                if (team.getIsFull()) {
                    throw new BusinessException("Невозможно одобрить — команда уже полная");
                }
            }
            case REJECTED -> {
                validateSenderIsTarget(app, requestSender);
            }
            case CANCELLED -> {
                if (!app.getStatus().toLowerCase().equals(ApplicationStatus.SENT.toString())) {
                    throw new BusinessException(
                            "Заявку можно отменить только если она находится в статусе Отправлено"
                    );
                }
                if (!sender.getUser().getId().equals(requestSender.getId())) {
                    throw new ForbiddenException("Только отправитель может отменить заявку");
                }
            }
            case SENT -> {
                validateCreate(dto, requestSender);
            }
            default -> throw new BusinessException("Неподдерживаемый статус заявки: " + dto.getStatus());
        }
    }

    private void validateSenderIsTarget(Application application, User sender) {
        Student actualTarget = studentService.findByIdOrElseThrow(application.getTargetId());
        if (!actualTarget.getUser().getId().equals(sender.getId())) {
            throw new ForbiddenException(ONLY_TARGET_CAN_ACCEPT_ERROR);
        }
        // TODO возможно стоит сделать покрасивее?
        if (application instanceof TeamRequest) {
            if (actualTarget.getCurrentTeam() == null
                    || !actualTarget.getCurrentTeam().getId().equals(application.getTeam().getId())) {
                throw new ForbiddenException(ONLY_TARGET_CAN_ACCEPT_ERROR);
            }
        }
    }

    private static final String ONLY_TARGET_CAN_ACCEPT_ERROR = "Принять заявку может только ее адресат";
}

