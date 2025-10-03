package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.dto.track.TrackCreationDto;
import ru.sfedu.teamselection.dto.track.TrackDto;
import ru.sfedu.teamselection.mapper.track.TrackDtoMapper;
import ru.sfedu.teamselection.service.TrackService;


@RestController
@RequestMapping("/api/v1/tracks")
@Tag(name = "TrackController", description = "API для работы с треками")
@RequiredArgsConstructor
@CrossOrigin
public class TrackController {

    private final TrackService trackService;
    private final TrackDtoMapper trackDtoMapper;

    private static final Logger LOGGER = Logger.getLogger(TrackController.class.getName());

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_BY_ID = "/{id}";
    public static final String FIND_ALL = "";
    public static final String CREATE_TRACK = "";
    public static final String UPDATE_TRACK = "/{id}";
    public static final String DELETE_TRACK = "/{id}";

    @Operation(
            method = "GET",
            summary = "Получение списка всех треков"
    )
    @GetMapping(FIND_ALL)
    @Auditable(auditPoint = "Track.FindAll")
    public List<TrackDto> findAll() {
        LOGGER.info("ENTER findAll() endpoint");
        return trackService.findAll();
    }

    @Operation(
            method = "GET",
            summary = "Получение трека по его id",
            parameters = {
                    @Parameter(name = "id", description = "id трека", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID)
    @Auditable(auditPoint = "Track.FindById")
    public ResponseEntity<TrackDto> findById(@PathVariable(name = "id") Long trackId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(trackId));
        TrackDto result = trackDtoMapper.mapToDto(trackService.findByIdOrElseThrow(trackId));
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "POST",
            summary = "Создание нового трека",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность трека"
            )
    )
    @PostMapping(CREATE_TRACK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "Track.CreateTrack")
    public ResponseEntity<TrackDto> createTrack(@RequestBody TrackCreationDto trackDto) {
        LOGGER.info("ENTER createTrack() endpoint");
        TrackDto result = trackDtoMapper.mapToDto(trackService.create(trackDto));
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "PUT",
            summary = "Изменить данные трека",
            description = "Изменяет информацию о треке. Эта операция доступна только администратору.",
            parameters = {
                    @Parameter(name = "id", description = "id трека", in = ParameterIn.PATH),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность трека"
            )
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(UPDATE_TRACK)
    @Auditable(auditPoint = "Track.UpdateTrack")
    public ResponseEntity<TrackDto> updateTrack(@PathVariable(value = "id") Long trackId,
                                @RequestBody TrackDto trackDto) {
        LOGGER.info("ENTER updateTrack(%d) endpoint".formatted(trackId));
        TrackDto result = trackDtoMapper.mapToDto(trackService.update(trackId, trackDto));
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "DELETE",
            summary = "Удалить трек по его id",
            parameters = {
                    @Parameter(name = "id", description = "id трека", in = ParameterIn.PATH),
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(DELETE_TRACK)
    @Auditable(auditPoint = "Track.DeleteTrack")
    public ResponseEntity<Void> deleteTrack(@PathVariable(value = "id") Long trackId) {
        LOGGER.info("ENTER deleteTrack(%d) endpoint".formatted(trackId));
        trackService.deleteById(trackId);
        return ResponseEntity.noContent().build();
    }
}
