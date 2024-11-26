package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.repository.TechnologyRepository;

@RestController
@RequestMapping()
@Tag(name = "TechnologyController",
        description = "API для взаимодействия с возможными технологиями студентов и проектов")
@RequiredArgsConstructor
public class TechnologyController {
    private final TechnologyRepository technologyRepository;

    public static final String FIND_ALL = "/api/v1/technologies";

    @Operation(
            method = "GET",
            summary = "Получение списка всех технологий"
    )
    @GetMapping(FIND_ALL) // checked
    public List<Technology> returnTags() {
        return technologyRepository.findAll();
    }
}
