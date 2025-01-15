package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.dto.student.StudentDto;



@RestController
@RequestMapping()
@Tag(name = "TracksController", description = "API для работы со треками")
@RequiredArgsConstructor
@CrossOrigin
public class TracksController {
    private static final Logger LOGGER = Logger.getLogger(TracksController.class.getName());
    public static final String PREFIX = "/api/v1/tracks";


    public static final String FIND_STUDENTS_BY_TRACK_TYPE = "/api/v1/tracks/{trackType}/students";
    public static final String FIND_CAPTAINS_IN_TRACK = "/api/v1/tracks/{trackId}/captains";
    public static final String FIND_STUDENTS_IN_TRACK = "/api/v1/tracks/{trackId}";

    /*
    ====================================================
    |     Перенесено из StudentController              |
    ====================================================
     */
    @Operation(
            method = "GET",
            summary = "Получение списка всех студентов по текущему треку в зависимости от типа: bachelor/master",
            parameters = { @Parameter(name = "type", description = "тип трека bachelor/master", in = ParameterIn.PATH)}
    )
    @GetMapping(FIND_STUDENTS_BY_TRACK_TYPE) //TODO
    public List<StudentDto> findStudentsByTrackType(@PathVariable(value = "trackType") String trackType) {
        LOGGER.info("ENTER getAllStudentsByCurrentTrack(%s) endpoint".formatted(trackType));
        throw new NotImplementedException();
    }

    @Operation(
            method = "GET",
            summary = "Получение списка всех капитанов команд по id трека",
            parameters = { @Parameter(name = "trackId", description = "id трека", in = ParameterIn.PATH)}
    )
    @GetMapping(FIND_CAPTAINS_IN_TRACK) // checked
    public List<StudentDto> findCaptainsInTrack(@PathVariable(value = "trackId") Long trackId) {
        LOGGER.info("ENTER findCaptainsInTrack(%d) endpoint".formatted(trackId));
        throw new NotImplementedException();
    }

    @Operation(
            method = "GET",
            summary = "Найти студентов по их статусу: состоят в команде или нет",
            parameters = {
                    @Parameter(name = "trackId", description = "id текущего трека", in = ParameterIn.PATH),
                    @Parameter(name = "status", description = "true/false")
            }
    )
    @GetMapping(FIND_STUDENTS_IN_TRACK) //checked
    public List<StudentDto> findStudentByStatusInTrack(@PathVariable(value = "trackId") Long trackId,
                                                          @RequestParam Boolean status) {
        LOGGER.info("ENTER findStudentByStatusInTrack(%d) endpoint".formatted(trackId));
        throw new NotImplementedException();
    }

    @Operation(
            method = "GET",
            summary = "Найти студентов по их навыкам(тегам)",
            parameters = {
                    @Parameter(name = "tags", description = "склеенная строка из тегов через пробелы"),
                    @Parameter(name = "trackId", description = "id текущего трека")
            }
    )
    @GetMapping("/findByTagAndTrackId") // checked
    public List<StudentDto> findStudentByTag(@RequestParam String tags, Long trackId) {
        LOGGER.info("ENTER findByTagAndTrackId() endpoint");
        throw new NotImplementedException();
    }
}
