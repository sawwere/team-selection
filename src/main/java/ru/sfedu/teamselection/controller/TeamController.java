package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.teamselection.dto.StudentDto;
import ru.sfedu.teamselection.dto.TeamDto;
import ru.sfedu.teamselection.mapper.StudentDtoMapper;
import ru.sfedu.teamselection.mapper.TeamDtoMapper;
import ru.sfedu.teamselection.service.ApplicationService;
import ru.sfedu.teamselection.service.TeamService;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping()
@Tag(name = "TeamController", description = "API для работы с командами")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamDtoMapper teamDtoMapper;
    private final StudentDtoMapper studentDtoMapper;

    private static final Logger LOGGER = Logger.getLogger(TeamController.class.getName());

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_BY_ID = "/api/v1/teams/{id}";
    public static final String FIND_BY_LIKE = "/api/v1/teams/like";

    public static final String SEARCH_TEAMS = "/api/v1/teams/search";
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_ALL = "/api/v1/teams";
    public static final String CREATE_TEAM = "/api/v1/teams";
    public static final String UPDATE_TEAM = "/api/v1/teams/{id}";
    public static final String DELETE_TEAM = "/api/v1/teams/{id}";

    public static final String FIND_SUBSCRIPTIONS_BY_ID = "/api/v1/teams/{id}/subscriptions";
    public static final String ADD_STUDENT_TO_TEAM = "api/v1/teams/{teamId}/students/{studentId}";

    private final ApplicationService applicationService;


    @Operation(
            method = "GET",
            summary = "Получение списка всех команд за все время"
    )
    @GetMapping(FIND_ALL) // checked
    public List<TeamDto> findAll() {
        LOGGER.info("ENTER findAll() endpoint");
        return teamService.findAll().stream().map(teamDtoMapper::mapToDto).toList();
    }

    @Operation(
            method = "DELETE",
            summary = "Удалить команду по ее id",
            parameters = {
                    @Parameter(name = "id", description = "id студента", in = ParameterIn.PATH),
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(DELETE_TEAM) // checked
    public void deleteTeam(@PathVariable(value = "id") Long teamId) {
        LOGGER.info("ENTER deleteTeam(%d) endpoint".formatted(teamId));
        teamService.delete(teamId);
    }

    @Operation(
            method = "GET",
            summary = "Получение команды по ее id",
            parameters = {
                    @Parameter(name = "id", description = "id команды", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID) // checked
    public TeamDto findById(@PathVariable(name = "id") Long teamId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(teamId));
        return teamDtoMapper.mapToDto(teamService.findByIdOrElseThrow(teamId));
    }

    @Operation(
            method = "GET",
            summary = "Найти заявки в команду",
            parameters = {
                    @Parameter(name = "id", description = "id команды", in = ParameterIn.PATH),
            })
    @GetMapping(FIND_SUBSCRIPTIONS_BY_ID) //checked
    public List<StudentDto> findSubscriptionsById(@PathVariable(value = "id") Long teamId) {
        LOGGER.info("ENTER findSubscriptionsById(%d) endpoint".formatted(teamId));
        return applicationService.findTeamApplications(teamId)
                .stream()
                .map(studentDtoMapper::mapToDto)
                .toList();
    }

    @Operation(
            method = "POST",
            summary = "Создание команды",
            parameters = { @Parameter(name = "team", description = "сущность команды")}
    )
    @PostMapping(CREATE_TEAM)
    public TeamDto createTeam(@RequestBody TeamDto team) {
        LOGGER.info("ENTER createUser() endpoint");
        return teamDtoMapper.mapToDto(teamService.create(team));
    }

    @Operation(
            method = "POST",
            summary = "Создание команды",
            parameters = { @Parameter(name = "team", description = "сущность команды")}
    )
    @PostMapping(ADD_STUDENT_TO_TEAM)
    public TeamDto addStudentToTeam(@RequestParam Long teamId, @RequestParam Long studentId) {
        LOGGER.info("ENTER addStudentToTeam() endpoint");
        return teamDtoMapper.mapToDto(teamService.addStudentToTeam(teamId, studentId));
    }

}
