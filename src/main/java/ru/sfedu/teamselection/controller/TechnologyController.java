package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.config.logging.Auditable;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.service.TechnologyService;

@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "TechnologyController",
        description = "API для взаимодействия с возможными технологиями студентов и проектов")
@RequiredArgsConstructor
@CrossOrigin
public class TechnologyController {

    private final TechnologyService technologyService;

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_ALL = "/api/v1/technologies";
    public static final String CREATE_TECHNOLOGY = "/api/v1/technologies";
    public static final String DELETE_TECHNOLOGY = "/api/v1/technologies/{id}";

    @Operation(
            method = "GET",
            summary = "Получение списка всех технологий"
    )
    @GetMapping(FIND_ALL) // checked
    @Auditable(auditPoint = "Technology.FindAll")
    public ResponseEntity<List<TechnologyDto>> findAll() {
        log.info("ENTER findAll() endpoint");
        List<TechnologyDto> result = technologyService.findAll();
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "POST",
            summary = "Создание новой технологии",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность технологии"
            )
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(CREATE_TECHNOLOGY)
    @Auditable(auditPoint = "Technology.CreateTechnology")
    public ResponseEntity<TechnologyDto> createTechnology(@RequestBody TechnologyDto technology) {
        log.info("ENTER createTechnology() endpoint");
        TechnologyDto result = technologyService.create(technology);
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "DELETE",
            summary = "Удаление технологии"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(DELETE_TECHNOLOGY)
    @Auditable(auditPoint = "Technology.DeleteTechnology")
    public ResponseEntity<String> deleteTechnology(
            @PathVariable("id") Long id
    ) {
        log.info("ENTER deleteTechnology() endpoint with id={}", id);
        technologyService.delete(id);
        return ResponseEntity.ok("Technology with id: " + id + " was deleted");
    }
}
