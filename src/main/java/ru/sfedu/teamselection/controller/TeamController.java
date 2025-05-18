package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.PageResponse;
import ru.sfedu.teamselection.dto.student.StudentDto;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.dto.team.TeamSearchOptionsDto;
import ru.sfedu.teamselection.dto.team.TeamUpdateDto;
import ru.sfedu.teamselection.mapper.PageResponseMapper;
import ru.sfedu.teamselection.mapper.student.StudentDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamDtoMapper;
import ru.sfedu.teamselection.service.ApplicationService;
import ru.sfedu.teamselection.service.TeamExportService;
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
    private final ApplicationService applicationService;

    private final TeamDtoMapper teamDtoMapper;
    private final StudentDtoMapper studentDtoMapper;
    private final PageResponseMapper pageResponseMapper;

    private static final Logger LOGGER = Logger.getLogger(TeamController.class.getName());

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_BY_ID = "/api/v1/teams/{id}";
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_ALL = "/api/v1/teams";
    public static final String SEARCH_TEAMS = "/api/v1/teams/search";
    public static final String CREATE_TEAM = "/api/v1/teams";
    public static final String UPDATE_TEAM = "/api/v1/teams/{id}";
    public static final String DELETE_TEAM = "/api/v1/teams/{id}";

    public static final String FIND_APPLICANTS_BY_ID = "/api/v1/teams/{id}/subscriptions";
    public static final String ADD_STUDENT_TO_TEAM = "/api/v1/teams/{teamId}/students/{studentId}";

    public static final String GET_SEARCH_OPTIONS = "/api/v1/teams/filters";

    private final TeamExportService teamExportService;


    @Operation(
            method = "GET",
            summary = "Получение списка возможных опций для поиска среди команд заданного трека"
    )
    @GetMapping(GET_SEARCH_OPTIONS)
    public ResponseEntity<TeamSearchOptionsDto> getSearchOptionsTeams(@RequestParam(value = "track_id") Long trackId) {
        TeamSearchOptionsDto result = teamService.getSearchOptionsTeams(trackId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "GET",
            summary = "Получение списка всех команд за все время"
    )
    @GetMapping(FIND_ALL) // checked
    public ResponseEntity<List<TeamDto>> findAll() {
        LOGGER.info("ENTER findAll() endpoint");
        List<TeamDto> result = teamService.findAll().stream().map(teamDtoMapper::mapToDto).toList();
        return ResponseEntity.ok(result);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    @Operation(
            method = "GET",
            summary = "Поиск команд с фильтрацией, пагинацией и сортировкой",
            parameters = {
                    @Parameter(name = "input", description = "строка из поиска", in = ParameterIn.QUERY),
                    @Parameter(name = "track_id", description = "ID трека", in = ParameterIn.QUERY),
                    @Parameter(name = "is_full", description = "Полностью ли укомплектована команда", in = ParameterIn.QUERY),
                    @Parameter(name = "project_type", description = "Тип проекта", in = ParameterIn.QUERY),
                    @Parameter(name = "technologies", description = "Список ID технологий", in = ParameterIn.QUERY),
                    @Parameter(name = "page", description = "Номер страницы", example = "0", in = ParameterIn.QUERY),
                    @Parameter(name = "size", description = "Размер страницы", example = "10", in = ParameterIn.QUERY),
                    @Parameter(name = "sort", description = "Сортировка (field,asc|desc)", example = "name,asc", in = ParameterIn.QUERY)
            })
    @GetMapping(SEARCH_TEAMS)
    public ResponseEntity<PageResponse<TeamDto>> search(
            @RequestParam(value = "input", required = false) String like,
            @RequestParam(value = "track_id", required = false) Long trackId,
            @RequestParam(value = "is_full", required = false) Boolean isFull,
            @RequestParam(value = "project_type", required = false) String projectType,
            @RequestParam(value = "technologies", required = false) List<Long> technologies,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<TeamDto> result = teamService.search(like, trackId, isFull, projectType, technologies, pageable)
                .map(teamDtoMapper::mapToDto);
        return ResponseEntity.ok(pageResponseMapper.toDto(result));
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
    public ResponseEntity<Void> deleteTeam(@PathVariable(value = "id") Long teamId) {
        LOGGER.info("ENTER deleteTeam(%d) endpoint".formatted(teamId));
        teamService.delete(teamId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            method = "GET",
            summary = "Получение команды по ее id",
            parameters = {
                    @Parameter(name = "id", description = "id команды", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID) // checked
    public ResponseEntity<TeamDto> findById(@PathVariable(name = "id") Long teamId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(teamId));
        TeamDto result = teamDtoMapper.mapToDto(teamService.findByIdOrElseThrow(teamId));
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "GET",
            summary = "Найти всех студентов, которые когда либо подавали заявку в команду",
            parameters = {
                    @Parameter(name = "id", description = "id команды", in = ParameterIn.PATH),
            })
    @GetMapping(FIND_APPLICANTS_BY_ID)
    public ResponseEntity<List<StudentDto>> findApplicantsById(@PathVariable(value = "id") Long teamId) {
        LOGGER.info("ENTER findApplicantsById(%d) endpoint".formatted(teamId));
        List<StudentDto> result = applicationService.findTeamApplicationsStudents(teamId)
                .stream()
                .map(studentDtoMapper::mapToDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/api/v1/teams/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportTeamsCsv(
            @RequestParam("trackId") Long trackId) {
        byte[] data = teamExportService.exportTeamsToCsvByTrack(trackId);
        String filename = "teams_track_" + trackId + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(data);
    }

    @GetMapping(value = "/api/v1/teams/export/excel", produces =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportTeamsExcel(
            @RequestParam("trackId") Long trackId) {
        byte[] data = teamExportService.exportTeamsToExcelByTrack(trackId);
        String filename = "teams_track_" + trackId + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @Operation(
            method = "POST",
            summary = "Создание команды",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность команды"
            )
    )
    @PostMapping(CREATE_TEAM)
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamCreationDto team) {
        LOGGER.info("ENTER createTeam() endpoint");
        TeamDto result = teamDtoMapper.mapToDto(teamService.create(team));
        return ResponseEntity.ok(result);
    }

    @Operation(
            method = "PUT",
            summary = "Добавление студента к команде",
            description = """
                Используется для принудительного добавления студента к определенной команды.

                Эта операция может быть выполнена только администратором ресурса.

                ВНИМАНИЕ: Данная операция не гарантирует изменения статуса заявок выбранного студента.
                При необходимости их статусы необходимо изменить вручную.
                """,
            tags = {"UNSAFE", "ADMIN"},
            parameters = {
                    @Parameter(name = "teamId", description = "Id команды", in = ParameterIn.PATH),
                    @Parameter(name = "studentId", description = "Id студента", in = ParameterIn.PATH),
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(ADD_STUDENT_TO_TEAM)
    public ResponseEntity<TeamDto> addStudentToTeam(@PathVariable Long teamId, @PathVariable Long studentId) {
        LOGGER.info("ENTER addStudentToTeam() endpoint");
        User user = userService.getCurrentUser();
        TeamDto result = teamDtoMapper.mapToDto(teamService.addStudentToTeam(teamId, studentId, user));
        return ResponseEntity.ok(result);
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
    @PutMapping(UPDATE_TEAM)
    public ResponseEntity<TeamDto> updateTeam(
            @PathVariable Long id,
            @RequestBody @Valid TeamUpdateDto dto
    ) {
        if (!id.equals(dto.getId())) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.getCurrentUser();
        Team updated = teamService.update(id, dto, user);
        TeamDto result = teamDtoMapper.mapToDto(updated);
        return ResponseEntity.ok(result);
    }
}
