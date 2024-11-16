package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.teamselection.dto.ApplicationDto;
import ru.sfedu.teamselection.mapper.ApplicationDtoMapper;
import ru.sfedu.teamselection.service.ApplicationService;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping()
@Tag(name = "ApplicationController", description = "API для работы с заявками")
@RequiredArgsConstructor
public class ApplicationController {


    private static final Logger LOGGER = Logger.getLogger(ApplicationController.class.getName());
    public static final String FIND_BY_ID = "/api/v1/applications/{id}";
    public static final String FIND_ALL = "/api/v1/applications";
    public static final String DELETE_APPLICATION = "/api/v1/applications/{id}";
    public static final String CREATE_APPLICATION = "/api/v1/applications";

    private final ApplicationService applicationService;
    private final ApplicationDtoMapper applicationDtoMapper;

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
    public ApplicationDto createApplication(@RequestBody ApplicationDto application) {
        LOGGER.info("ENTER createApplication() endpoint");
        return applicationDtoMapper.mapToDto(applicationService.create(application));
    }

    @Operation(
            method = "GET",
            summary = "Получение заявки по его id",
            parameters = {
                    @Parameter(name = "id", description = "id заявки", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID) // checked
    public ApplicationDto findById(@PathVariable(name = "id") Long applicationId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(applicationId));
        return applicationDtoMapper.mapToDto(applicationService.findByIdOrElseThrow(applicationId));
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





}
