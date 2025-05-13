package ru.sfedu.teamselection.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.exception.BusinessException;
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
        var student = studentService.findByIdOrElseThrow(dto.getStudentId());
        if (!student.getUser().getId().equals(sender.getId())) {
            throw new BusinessException("Вы не можете подать заявку от имени другого студента");
        }
        if (student.getHasTeam()) {
            throw new BusinessException("Студент уже состоит в команде");
        }

        var team = teamService.findByIdOrElseThrow(dto.getTeamId());
        if (team.getIsFull()) {
            throw new BusinessException("Невозможно подать заявку — команда полная");
        }
        if (student.getCourse() == 2 &&
                teamService.getSecondYearsCount(team) >= team.getCurrentTrack().getMaxSecondCourseConstraint()) {
            throw new BusinessException("Невозможно — в команде уже максимальное число второкурсников");
        }
        if (!studentService.typeOfStudentTrack(student).equals(team.getCurrentTrack().getType())) {
            throw new BusinessException("Невозможно — неверный трек");
        }
    }

    public void validateUpdate(ApplicationCreationDto dto, User sender, Application app) {
        if (app == null) {
            throw new ResourceNotFoundException("Application", dto.getId());
        }
        var student = app.getStudent();
        switch (dto.getStatus()) {
            case ACCEPTED -> {
                var team = app.getTeam();
                if (!student.getUser().getId().equals(sender.getId())) {
                    throw new BusinessException("Только капитан команды может одобрить заявку");
                }
                if (team.getIsFull()) {
                    throw new BusinessException("Невозможно одобрить — команда уже полная");
                }
            }
            case REJECTED -> {
                var team = app.getTeam();
                if (!student.getUser().getId().equals(sender.getId())) {
                    throw new BusinessException("Только капитан команды может отклонить заявку");
                }
            }
            case CANCELLED -> {

                if (!student.getUser().getId().equals(sender.getId())) {
                    throw new BusinessException("Только отправитель может отменить заявку");
                }
            }
            case SENT -> {
                validateCreate(dto, sender);
            }
            default -> throw new BusinessException("Неподдерживаемый статус заявки: " + dto.getStatus());
        }
    }
}

