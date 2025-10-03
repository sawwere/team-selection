package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.config.logging.Auditable;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.dto.application.ApplicationResponseDto;
import ru.sfedu.teamselection.mapper.application.ApplicationDtoMapper;
import ru.sfedu.teamselection.mapper.application.ApplicationMapper;
import ru.sfedu.teamselection.service.ApplicationService;
import ru.sfedu.teamselection.service.UserService;


@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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

    @Operation(method = "GET", summary = "Получение списка заявок с пагинацией, сортировкой и фильтром по треку")
    @GetMapping(FIND_ALL)
    @Auditable(auditPoint = "Application.FindAll")
    public ResponseEntity<Page<ApplicationDto>> findAll(
            @RequestParam(name = "track_id", required = false) Long trackId,
            @RequestParam(name = "status", required = false) String status,
            @ParameterObject
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        LOGGER.info("ENTER findAll(trackId=" + trackId + ", pageable=" + pageable + ")");
        Page<ApplicationDto> page = applicationService
                .findAll(trackId, status, pageable)
                .map(applicationDtoMapper::mapToDto);
        return ResponseEntity.ok(page);
    }

    @Operation(
            method = "POST",
            summary = "Создание заявки"
    )
    @PostMapping(CREATE_APPLICATION)
    @Auditable(auditPoint = "Application.CreateApplication")
    public ResponseEntity<ApplicationCreationDto> createApplication(@RequestBody ApplicationCreationDto application) {
        LOGGER.info("ENTER createApplication() endpoint");
        User user = userService.getCurrentUser();
        ApplicationCreationDto result = applicationMapper.mapToCreationDto(
                applicationService.create(application, user)
        );
        return ResponseEntity.ok(result);
    }



    @Operation(
            method = "PUT",
            summary = "Обновление статуса заявки"
    )
    @PutMapping(UPDATE_APPLICATION)
    @Auditable(auditPoint = "Application.Update")
    public ResponseEntity<ApplicationCreationDto> update(
            @RequestBody ApplicationCreationDto dto
    ) {
        User current = userService.getCurrentUser();
        ApplicationCreationDto result = applicationMapper.mapToCreationDto(
                applicationService.update(dto, current)
        );
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "GET",
            summary = "Получение заявки по его id",
            parameters = {
                    @Parameter(name = "id", description = "id заявки", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID)
    @Auditable(auditPoint = "Application.FindById")
    public ResponseEntity<ApplicationCreationDto> findById(@PathVariable(name = "id") Long applicationId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(applicationId));
        ApplicationCreationDto result = applicationMapper.mapToCreationDto(
                applicationService.findByIdOrElseThrow(applicationId)
        );
        return ResponseEntity.ok(result);
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
    @Auditable(auditPoint = "Application.Delete")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
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
    @Auditable(auditPoint = "Application.FindByTeamAndStudent")
    public ResponseEntity<ApplicationResponseDto> findByTeamAndStudent(
            @PathVariable Long teamId,
            @PathVariable Long studentId
    ) {
        LOGGER.info("ENTER findByTeamAndStudent(%d, %d) endpoint".formatted(teamId, studentId));
        User current = userService.getCurrentUser();
        ApplicationResponseDto result = applicationService.findByTeamAndStudentOrElseThrow(teamId, studentId, current);
        return ResponseEntity.ok(result);
    }


}
