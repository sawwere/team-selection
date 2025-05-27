package ru.sfedu.teamselection.controller.utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.teamselection.dto.team.ProjectTypeDto;
import ru.sfedu.teamselection.mapper.ProjectTypeMapper;
import ru.sfedu.teamselection.repository.ProjectTypeRepository;

@RestController
@RequestMapping()
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
    @GetMapping(FIND_ALL) // checked
    public ResponseEntity<List<ProjectTypeDto>> findAll() {
        List<ProjectTypeDto> result = projectTypeDtoMapper.mapListToDto(projectTypeRepository.findAll());
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "GET",
            summary = "Создание нового типа проекта"
    )
    @PostMapping(FIND_ALL) // checked
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
