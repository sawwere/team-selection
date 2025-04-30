package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.teamselection.dto.student.StudentDto;
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
    public TrackDto findById(@PathVariable(name = "id") Long trackId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(trackId));
        return trackDtoMapper.mapToDto(trackService.findByIdOrElseThrow(trackId));
    }

    @Operation(
            method = "POST",
            summary = "Создание нового трека",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность трека"
            )
    )
    @PostMapping(CREATE_TRACK)
    public TrackDto createTrack(@RequestBody TrackCreationDto trackDto) {
        LOGGER.info("ENTER createTrack() endpoint");
        return trackDtoMapper.mapToDto(trackService.create(trackDto));
    }

    @Operation(
            method = "PUT",
            summary = "Изменить данные трека",
            description = "Изменяет информацию о треке. Эта операция доступна только администратору.",
            tags = {"UNSAFE", "ADMIN"},
            parameters = {
                    @Parameter(name = "id", description = "id трека", in = ParameterIn.PATH),
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность трека"
            )
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(UPDATE_TRACK)
    public TrackDto updateTrack(@PathVariable(value = "id") Long trackId,
                                @RequestBody TrackDto trackDto) {
        LOGGER.info("ENTER updateTrack(%d) endpoint".formatted(trackId));
        return trackDtoMapper.mapToDto(trackService.update(trackId, trackDto));
    }

    @Operation(
            method = "DELETE",
            summary = "Удалить трек по его id",
            parameters = {
                    @Parameter(name = "id", description = "id трека", in = ParameterIn.PATH),
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(DELETE_TRACK)
    public void deleteTrack(@PathVariable(value = "id") Long trackId) {
        LOGGER.info("ENTER deleteTrack(%d) endpoint".formatted(trackId));
        trackService.deleteById(trackId);
    }
}
