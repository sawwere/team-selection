package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.api.TrackApi;
import ru.sfedu.teamselection.config.logging.Auditable;
import ru.sfedu.teamselection.dto.track.TrackCreationDto;
import ru.sfedu.teamselection.dto.track.TrackDto;
import ru.sfedu.teamselection.mapper.track.TrackDtoMapper;
import ru.sfedu.teamselection.service.TrackService;


@RestController
@RequestMapping(value = "/api/v1/tracks", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "TrackController", description = "API для работы с треками")
@RequiredArgsConstructor
@CrossOrigin
public class TrackController implements TrackApi {

    private final TrackService trackService;
    private final TrackDtoMapper trackDtoMapper;

    private static final Logger LOGGER = Logger.getLogger(TrackController.class.getName());

    @Override
    @Auditable(auditPoint = "Track.FindAll")
    public ResponseEntity<List<TrackDto>> findAllTracks() {
        LOGGER.info("ENTER findAll() endpoint");
        return ResponseEntity.ok(trackService.findAll());
    }

    @Override
    @Auditable(auditPoint = "Track.FindById")
    public ResponseEntity<TrackDto> findTrackById(@PathVariable(name = "trackId") Long trackId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(trackId));
        TrackDto result = trackDtoMapper.mapToDto(trackService.findByIdOrElseThrow(trackId));
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "Track.CreateTrack")
    public ResponseEntity<TrackDto> createTrack(@RequestBody TrackCreationDto trackDto) {
        LOGGER.info("ENTER createTrack() endpoint");
        TrackDto result = trackDtoMapper.mapToDto(trackService.create(trackDto));
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "Track.UpdateTrack")
    public ResponseEntity<TrackDto> updateTrack(
            @PathVariable(value = "trackId") Long trackId,
            @RequestBody TrackDto trackDto
    ) {
        LOGGER.info("ENTER updateTrack(%d) endpoint".formatted(trackId));
        TrackDto result = trackDtoMapper.mapToDto(trackService.update(trackId, trackDto));
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "Track.DeleteTrack")
    public ResponseEntity<Void> deleteTrack(@PathVariable(value = "trackId") Long trackId) {
        LOGGER.info("ENTER deleteTrack(%d) endpoint".formatted(trackId));
        trackService.deleteById(trackId);
        return ResponseEntity.noContent().build();
    }
}
