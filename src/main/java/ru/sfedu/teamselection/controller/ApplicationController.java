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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.mapper.application.ApplicationCreationDtoMapper;
import ru.sfedu.teamselection.mapper.application.ApplicationDtoMapper;
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

    private final ApplicationService applicationService;
    private final ApplicationCreationDtoMapper applicationCreationDtoMapper;

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
    @PostMapping(CREATE_APPLICATION) // checked
    public ApplicationCreationDto createApplication(@RequestBody ApplicationCreationDto application) {
        LOGGER.info("ENTER createApplication() endpoint");
        User user = userService.getCurrentUser();
        return applicationCreationDtoMapper.mapToDto(applicationService.create(application, user));
    }

    @Operation(
            method = "PUT",
            summary = "Обновление статуса заявки",
            parameters = {@Parameter(name = "application", description = "DTO с обновлённой информацией о заявке")}
    )
    @PutMapping(UPDATE_APPLICATION)
    public ApplicationCreationDto updateApplication(@RequestBody ApplicationCreationDto applicationDto) {
        LOGGER.info("ENTER updateApplication() endpoint");
        User user = userService.getCurrentUser();
        try {
            return applicationCreationDtoMapper.mapToDto(applicationService.update(applicationDto, user));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
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
        LOGGER.info("ENTER deleteApplication(%d) endpoint".formatted(applicationId));
        applicationService.delete(applicationId);
    }
}
