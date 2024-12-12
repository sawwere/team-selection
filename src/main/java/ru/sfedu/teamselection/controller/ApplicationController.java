package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.ApplicationCreationDto;
import ru.sfedu.teamselection.mapper.application.ApplicationCreationDtoMapper;
import ru.sfedu.teamselection.service.ApplicationService;


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

    private final ApplicationService applicationService;
    private final ApplicationCreationDtoMapper applicationCreationDtoMapper;

    @Operation(
            method = "GET",
            summary = "Получение списка всех заявок за все время"
    )
    @GetMapping(FIND_ALL) // checked
    public List<ApplicationCreationDto> findAll() {
        LOGGER.info("ENTER findAll() endpoint");
        return applicationService.findAll().stream().map(applicationCreationDtoMapper::mapToDto).toList();
    }

    @Operation(
            method = "POST",
            summary = "Создание заявки",
            parameters = { @Parameter(name = "application", description = "сущность заявки")}
    )
    @PostMapping(CREATE_APPLICATION) // checked
    public ApplicationCreationDto createApplication(@RequestBody ApplicationCreationDto application) {
        LOGGER.info("ENTER createApplication() endpoint");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applicationCreationDtoMapper.mapToDto(applicationService.create(application, user));
    }

    @Operation(
            method = "GET",
            summary = "Получение заявки по его id",
            parameters = {
                    @Parameter(name = "id", description = "id заявки", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID) // checked
    public ApplicationCreationDto findById(@PathVariable(name = "id") Long applicationId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(applicationId));
        return applicationCreationDtoMapper.mapToDto(applicationService.findByIdOrElseThrow(applicationId));
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
    public void deleteApplication(@PathVariable(value = "id") Long applicationId) {
        LOGGER.info("ENTER deleteStudent(%d) endpoint".formatted(applicationId));
        applicationService.delete(applicationId);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(DELETE_APPLICATION)
    public void findApplicationByTeamIdAndStudentId(@PathVariable(value = "id") Long applicationId) {
        LOGGER.info("ENTER deleteStudent(%d) endpoint".formatted(applicationId));
        applicationService.delete(applicationId);
    }

}
