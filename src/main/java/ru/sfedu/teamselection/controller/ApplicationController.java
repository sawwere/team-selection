package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.mapper.application.ApplicationDtoMapper;
import ru.sfedu.teamselection.mapper.application.ApplicationMapper;
import ru.sfedu.teamselection.service.ApplicationService;
import ru.sfedu.teamselection.service.UserService;


@RestController
@RequestMapping()
@Tag(name = "ApplicationController", description = "API для работы с заявками")
@RequiredArgsConstructor
public class ApplicationController {
    private static final Logger LOGGER = Logger.getLogger(ApplicationController.class.getName());
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_BY_ID = "/api/v1/applications/{id}";
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_ALL = "/api/v1/applications";
    public static final String DELETE_APPLICATION = "/api/v1/applications/{id}";
    public static final String CREATE_APPLICATION = "/api/v1/applications";
    public static final String UPDATE_APPLICATION = "/api/v1/applications";

    public static final String FIND_BY_TEAM_AND_STUDENT =
            "/api/v1/applications/team/{teamId}/student/{studentId}";

    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper;

    private final ApplicationDtoMapper applicationDtoMapper;

    private final UserService userService;

    @Operation(
            method = "GET",
            summary = "Получение списка всех заявок за все время"
    )
    @GetMapping(FIND_ALL) // checked
    public List<ApplicationDto> findAll() {
        LOGGER.info("ENTER findAll() endpoint");
        return applicationService.findAll().stream().map(applicationDtoMapper::mapToDto).toList();
    }

    @Operation(
            method = "POST",
            summary = "Создание заявки",
            parameters = { @Parameter(name = "application", description = "сущность заявки")}
    )
    @PostMapping(CREATE_APPLICATION)
    public ApplicationCreationDto createApplication(@RequestBody ApplicationCreationDto application) {
        LOGGER.info("ENTER createApplication() endpoint");
        User user = userService.getCurrentUser();
        return applicationMapper.mapToCreationDto(applicationService.create(application, user));
    }



    @Operation(
            method = "PUT",
            summary = "Обновление статуса заявки",
            parameters = {@Parameter(name = "application", description = "DTO с обновлённой информацией о заявке")}
    )
    @PutMapping(UPDATE_APPLICATION)
    public ApplicationCreationDto update(
            @RequestBody ApplicationCreationDto dto
    ) {
        User current = userService.getCurrentUser();
        return applicationMapper.mapToCreationDto(
                applicationService.update(dto, current)
        );
    }

    @Operation(
            method = "GET",
            summary = "Получение заявки по его id",
            parameters = {
                    @Parameter(name = "id", description = "id заявки", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID)
    public ApplicationCreationDto findById(@PathVariable(name = "id") Long applicationId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(applicationId));
        return applicationMapper.mapToCreationDto(applicationService.findByIdOrElseThrow(applicationId));
    }

    @Operation(
            method = "DELETE",
            summary = "Удалить заявку по его id",
            parameters = {
                    @Parameter(name = "id", description = "id заявки", in = ParameterIn.PATH),
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(DELETE_APPLICATION) // checked
    public void delete(@PathVariable Long id) {
        applicationService.delete(id);
    }

    @Operation(
            method = "GET",
            summary = "Получение заявки по id команды и id студента",
            parameters = {
                    @Parameter(name = "teamId", description = "id команды", in = ParameterIn.PATH),
                    @Parameter(name = "studentId", description = "id студента", in = ParameterIn.PATH)
            }
    )
    @GetMapping(FIND_BY_TEAM_AND_STUDENT)
    public ApplicationCreationDto findByTeamAndStudent(
            @PathVariable Long teamId,
            @PathVariable Long studentId
    ) {
        LOGGER.info("ENTER findByTeamAndStudent(%d, %d) endpoint".formatted(teamId, studentId));
        return applicationMapper.mapToCreationDto(
                applicationService.findByTeamAndStudentOrElseThrow(teamId, studentId)
        );
    }


}
