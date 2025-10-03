package ru.sfedu.teamselection.controller.utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import ru.sfedu.teamselection.dto.team.ProjectTypeDto;
import ru.sfedu.teamselection.mapper.ProjectTypeMapper;
import ru.sfedu.teamselection.repository.ProjectTypeRepository;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "ProjectTypeController",
        description = "API для операций с типами проектов")
@RequiredArgsConstructor
@CrossOrigin
public class ProjectTypeController {
    private final ProjectTypeRepository projectTypeRepository;
    private final ProjectTypeMapper projectTypeDtoMapper;

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String CREATE = "/api/v1/projectTypes";
    public static final String FIND_ALL = "/api/v1/projectTypes";
    public static final String DELETE_PROJECT_TYPE = "/api/v1/projectTypes/{id}";

    @Operation(
            method = "GET",
            summary = "Получение списка всех возможных типов проектов"
    )
    @GetMapping(FIND_ALL)
    @Auditable(auditPoint = "ProjectType.FindAll")
    public ResponseEntity<List<ProjectTypeDto>> findAll() {
        List<ProjectTypeDto> result = projectTypeDtoMapper.mapListToDto(projectTypeRepository.findAll());
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "GET",
            summary = "Создание нового типа проекта"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(CREATE)
    @Auditable(auditPoint = "ProjectType.Create")
    public ResponseEntity<ProjectTypeDto> create(@RequestBody @Valid ProjectTypeDto projectTypeDto) {
        ProjectTypeDto result = projectTypeDtoMapper.mapToDto(
                projectTypeRepository.save(projectTypeDtoMapper.mapToEntity(projectTypeDto))
        );
        return ResponseEntity.ok(result);
    }

    @Operation(
            method  = "DELETE",
            summary = "Удаление типа проекта"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(DELETE_PROJECT_TYPE)
    @Auditable(auditPoint = "ProjectType.Delete")
    public ResponseEntity<String> deleteProjectType(
            @PathVariable("id") Long id
    ) {
        if (!projectTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        projectTypeRepository.deleteById(id);

        return ResponseEntity.ok("Project type with id: " + id + "was deleted");
    }
}
