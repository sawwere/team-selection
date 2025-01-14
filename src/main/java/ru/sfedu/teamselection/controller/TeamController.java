package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.StudentDto;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.dto.team.TeamSearchOptionsDto;
import ru.sfedu.teamselection.mapper.student.StudentDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamDtoMapper;
import ru.sfedu.teamselection.service.ApplicationService;
import ru.sfedu.teamselection.service.TeamService;
import ru.sfedu.teamselection.service.UserService;

@RestController
@RequestMapping()
@Tag(name = "TeamController", description = "API для работы с командами")
@RequiredArgsConstructor
@CrossOrigin
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    private final TeamDtoMapper teamDtoMapper;
    private final StudentDtoMapper studentDtoMapper;

    private static final Logger LOGGER = Logger.getLogger(TeamController.class.getName());

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_BY_ID = "/api/v1/teams/{id}";
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_ALL = "/api/v1/teams";
    public static final String SEARCH_TEAMS = "/api/v1/teams/search";
    public static final String CREATE_TEAM = "/api/v1/teams";
    public static final String UPDATE_TEAM = "/api/v1/teams/{id}";
    public static final String DELETE_TEAM = "/api/v1/teams/{id}";

    public static final String FIND_SUBSCRIPTIONS_BY_ID = "/api/v1/teams/{id}/subscriptions";
    public static final String ADD_STUDENT_TO_TEAM = "api/v1/teams/{teamId}/students/{studentId}";

    public static final String GET_SEARCH_OPTIONS = "/api/v1/teams/filters";


    @Operation(
            method = "GET",
            summary = "Получение списка возможных опций для поиска среди команд заданного трека"
    )
    @GetMapping(GET_SEARCH_OPTIONS)

    public TeamSearchOptionsDto getSearchOptionsTeams(@RequestParam(value = "track_id") Long trackId) {
        return teamService.getSearchOptionsTeams(trackId);
    }

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
            method = "GET",
            summary = "Поиск студентов с фильтрацией по полям",
            parameters = {
                    @Parameter(name = "input",
                            description = "строка из поиска, разделенная пробелами",
                            in = ParameterIn.QUERY),
                    @Parameter(name = "track_id",
                            description = "К какому треку принадлежит команда",
                            in = ParameterIn.QUERY),
                    @Parameter(name = "is_full",
                            description = "Полностью ли ли укомплектована команда",
                            in = ParameterIn.QUERY),
                    @Parameter(name = "project_type",
                            description = "Тип проекта, указанный капитаном",
                            in = ParameterIn.QUERY),
                    @Parameter(name = "technologies",
                            description = "Список технологий(умений) команды",
                            in = ParameterIn.QUERY),
            })
    @GetMapping(SEARCH_TEAMS)
    public List<TeamDto> search(
            @RequestParam(value = "input", required = false) String like,
            @RequestParam(value = "track_id", required = false) Long trackId,
            @RequestParam(value = "is_full", required = false) Boolean isFull,
            @RequestParam(value = "project_type", required = false) String projectType,
            @RequestParam(value = "technologies", required = false) List<Long> technologies
    ) {
        LOGGER.info("ENTER search() endpoint");
        return teamService.search(like, trackId, isFull, projectType, technologies)
                .stream()
                .map(teamDtoMapper::mapToDto)
                .toList();
    }

    @Operation(
            method = "DELETE",
            summary = "Удалить команду по ее id",
            parameters = {
                    @Parameter(name = "id", description = "id команды", in = ParameterIn.PATH),
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
    public TeamDto createTeam(@RequestBody TeamCreationDto team) {
        LOGGER.info("ENTER createUser() endpoint");
        return teamDtoMapper.mapToDto(teamService.create(team));
    }

    @Operation(
            method = "PUT",
            summary = "Добавление студента к команде"
    )
    @PutMapping(ADD_STUDENT_TO_TEAM)
    public TeamDto addStudentToTeam(@PathVariable Long teamId, @PathVariable Long studentId) {
        LOGGER.info("ENTER addStudentToTeam() endpoint");
        return teamDtoMapper.mapToDto(teamService.addStudentToTeam(teamId, studentId));
    }

    @Operation(
            method = "PUT",
            summary = "Изменить данные команды",
            description = """
                Используется для модификации данных определенной команды.

                Эта операция может быть выполнена капитаном команды,
                либо администратором ресурса.

                Недоступные для обновления поля будут проигнорированы.

                ВНИМАНИЕ: Список обновляемых полей отличается в зависимости от того, кто отправил запрос -
                администратору доступны для изменения все поля, включая те, что зависят от состояния других таблиц,
                поэтому редактировать их нужно ОСТОРОЖНО.
                """,
            tags = {"UNSAFE"},
            parameters = {
                    @Parameter(name = "id", description = "Id команды", in = ParameterIn.PATH)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность команды"
            ))
    @PutMapping(UPDATE_TEAM) // checked
    public TeamDto updateTeam(@PathVariable(value = "id") Long teamId,
                                    @RequestBody TeamDto team) {
        LOGGER.info("ENTER updateTeam(%d) endpoint".formatted(teamId));
        User user = userService.getCurrentUser();
        return teamDtoMapper.mapToDto(teamService.update(teamId, team, user));
    }
}
