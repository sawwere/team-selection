package ru.sfedu.teamselection.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.api.TechnologyApi;
import ru.sfedu.teamselection.config.logging.Auditable;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.service.TechnologyService;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class TechnologyController implements TechnologyApi {

    private final TechnologyService technologyService;

    @Auditable(auditPoint = "Technology.FindAll")
    public ResponseEntity<List<TechnologyDto>> findAllTechnologies() {
        List<TechnologyDto> result = technologyService.findAll();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "Technology.CreateTechnology")
    public ResponseEntity<TechnologyDto> createTechnology(@RequestBody TechnologyDto technology) {
        TechnologyDto result = technologyService.create(technology);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "Technology.DeleteTechnology")
    public ResponseEntity<String> deleteTechnology(
            @PathVariable("id") Long id
    ) {
        technologyService.delete(id);
        return ResponseEntity.ok("Technology with id: " + id + " was deleted");
    }
}
