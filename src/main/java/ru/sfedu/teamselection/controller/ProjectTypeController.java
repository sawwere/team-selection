package ru.sfedu.teamselection.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.api.ProjectTypeApi;
import ru.sfedu.teamselection.config.logging.Auditable;
import ru.sfedu.teamselection.dto.ProjectTypeDto;
import ru.sfedu.teamselection.mapper.ProjectTypeMapper;
import ru.sfedu.teamselection.repository.ProjectTypeRepository;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ProjectTypeController implements ProjectTypeApi {
    private final ProjectTypeRepository projectTypeRepository;
    private final ProjectTypeMapper projectTypeDtoMapper;

    @Override
    @Auditable(auditPoint = "ProjectType.FindAll")
    public ResponseEntity<List<ProjectTypeDto>> findAllProjectTypes() {
        List<ProjectTypeDto> result = projectTypeDtoMapper.mapListToDto(projectTypeRepository.findAll());
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "ProjectType.Create")
    public ResponseEntity<ProjectTypeDto> createProjectType(@RequestBody @Valid ProjectTypeDto projectTypeDto) {
        ProjectTypeDto result = projectTypeDtoMapper.mapToDto(
                projectTypeRepository.save(projectTypeDtoMapper.mapToEntity(projectTypeDto))
        );
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "ProjectType.Delete")
    public ResponseEntity<String> deleteProjectType(
            @PathVariable("id") Long id
    ) {
        if (!projectTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        projectTypeRepository.deleteById(id);

        return ResponseEntity.ok("Project type with id: " + id + " was deleted");
    }
}
