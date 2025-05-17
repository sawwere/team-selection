package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.repository.TechnologyRepository;

@Slf4j
@RestController
@RequestMapping()
@Tag(name = "TechnologyController",
        description = "API для взаимодействия с возможными технологиями студентов и проектов")
@RequiredArgsConstructor
@CrossOrigin
public class TechnologyController {
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_ALL = "/api/v1/technologies";
    public static final String CREATE_TECHNOLOGY = "/api/v1/technologies";

    private final TechnologyRepository technologyRepository;

    private final TechnologyMapper technologyDtoMapper;

    @Operation(
            method = "GET",
            summary = "Получение списка всех технологий"
    )
    @GetMapping(FIND_ALL) // checked
    public ResponseEntity<List<TechnologyDto>> findAll() {
        log.info("ENTER findAll() endpoint");
        List<TechnologyDto> result = technologyDtoMapper.mapListToDto(technologyRepository.findAll());
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "POST",
            summary = "Создание новой технологии",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность технологии"
            )
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(CREATE_TECHNOLOGY)
    public ResponseEntity<TechnologyDto> createTechnology(@RequestBody TechnologyDto technology) {
        log.info("ENTER createTechnology() endpoint");
        TechnologyDto result = technologyDtoMapper.mapToDto(
                technologyRepository.save(technologyDtoMapper.mapToEntity(technology))
        );
        return ResponseEntity.ok(result);
    }
}
