package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.student.StudentCreationDto;
import ru.sfedu.teamselection.dto.student.StudentDto;
import ru.sfedu.teamselection.dto.student.StudentSearchOptionsDto;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.mapper.student.StudentDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamDtoMapper;
import ru.sfedu.teamselection.service.StudentService;
import ru.sfedu.teamselection.service.TeamService;
import ru.sfedu.teamselection.service.UserService;


@RestController
@RequestMapping()
@Tag(name = "StudentController", description = "API для работы со студентами")
@RequiredArgsConstructor
@CrossOrigin
public class StudentController {
    private static final Logger LOGGER = Logger.getLogger(StudentController.class.getName());

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_BY_ID = "/api/v1/students/{id}";
    public static final String SEARCH_STUDENTS = "/api/v1/students/search";
    public static final String GET_STUDENT_ID_BY_CURRENT_USER = "/api/v1/students/me";
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String FIND_ALL = "/api/v1/students";
    public static final String CREATE_STUDENT = "/api/v1/students";
    public static final String UPDATE_STUDENT = "/api/v1/students/{id}";
    public static final String DELETE_STUDENT = "/api/v1/students/{id}";
    public static final String FIND_TEAM_HISTORY = "/api/v1/students/{id}/teams";
    public static final String GET_SEARCH_OPTIONS = "/api/v1/students/filters";

    private final TeamService teamService;
    private final StudentService studentService;
    private final  UserService userService;

    private final StudentDtoMapper studentDtoMapper;
    private final TeamDtoMapper teamDtoMapper;

    @Operation(
            method = "GET",
            summary = "Получение списка возможных опций для поиска среди студентов"
    )
    @GetMapping(GET_SEARCH_OPTIONS)

    public StudentSearchOptionsDto getSearchOptionsStudents() {
        return studentService.getSearchOptionsStudents();
    }

    @Operation(
            method = "GET",
            summary = "Поиск студентов с фильтрацией, пагинацией и сортировкой",
            parameters = {
                    @Parameter(name = "input", description = "строка из поиска", in = ParameterIn.QUERY),
                    @Parameter(name = "course", description = "Курс обучения", in = ParameterIn.QUERY),
                    @Parameter(name = "group_number", description = "Номер группы", in = ParameterIn.QUERY),
                    @Parameter(name = "has_team", description = "Состоит ли в команде", in = ParameterIn.QUERY),
                    @Parameter(name = "is_captain", description = "Является ли капитаном", in = ParameterIn.QUERY),
                    @Parameter(name = "technologies", description = "Список ID технологий", in = ParameterIn.QUERY),
                    @Parameter(name = "page", description = "Номер страницы", example = "0", in = ParameterIn.QUERY),
                    @Parameter(name = "size", description = "Размер страницы", example = "10", in = ParameterIn.QUERY),
                    @Parameter(name = "sort", description = "Сортировка (field,asc|desc)", example = "name,asc", in = ParameterIn.QUERY)
            })
    @GetMapping(SEARCH_STUDENTS)
    public Page<StudentDto> search(
            @RequestParam(value = "input", required = false) String like,
            @RequestParam(value = "course", required = false) Integer course,
            @RequestParam(value = "group_number", required = false) Integer groupNumber,
            @RequestParam(value = "has_team", required = false) Boolean hasTeam,
            @RequestParam(value = "is_captain", required = false) Boolean isCaptain,
            @RequestParam(value = "track_id", required = false) Long trackId,
            @RequestParam(value = "technologies", required = false) List<Long> technologies,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        return studentService.search(like, trackId, course, groupNumber, hasTeam, isCaptain, technologies, pageable)
                .map(studentDtoMapper::mapToDto);
    }


    @Operation(
            method = "GET",
            summary = "Получение списка всех студентов за все время"
    )
    @GetMapping(FIND_ALL) // checked
    public List<StudentDto> findAll() {
        LOGGER.info("ENTER findAll() endpoint");
        return studentService.findAll().stream().map(studentDtoMapper::mapToDto).toList();
    }

    @Operation(
            method = "POST",
            summary = "Создание студента",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность студента"
            ))
    @PostMapping(CREATE_STUDENT) // checked
    public StudentDto createStudent(@RequestBody StudentCreationDto student) {
        LOGGER.info("ENTER createUser() endpoint");
        return studentDtoMapper.mapToDto(studentService.create(student));
    }

    @Operation(
            method = "PUT",
            summary = "Изменить данные студента",
            description = """
                Используется для модификации данных определенного пользователя.

                Эта операция может быть выполнена самим пользователем (редактирование информации о себе),
                либо администратором ресурса.

                Недоступные для обновления поля будут проигнорированы.

                ВНИМАНИЕ: Список обновляемых полей отличается от того, кто отправил запрос -
                администратору доступны для изменения все поля, включая те, что зависят от состояния других таблиц,
                поэтому редактировать их нужно ОСТОРОЖНО.
                """,
            tags = {"UNSAFE"},
            parameters = {
                    @Parameter(name = "id", description = "Id студента", in = ParameterIn.PATH)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность студента"
            ))
    @PreAuthorize("hasAuthority('ADMIN') or @studentService.getCurrentStudent().equals(#studentId)")
    @PutMapping(value = UPDATE_STUDENT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentDto updateStudent(@PathVariable(value = "id") Long studentId,
                                  @RequestBody StudentDto student) {
        LOGGER.info("ENTER updateStudent(%d) endpoint".formatted(studentId));
        User user = userService.getCurrentUser();
        boolean isUnsafeAllowed = user.getRole().getName().equals("ADMIN");
        return studentDtoMapper.mapToDto(studentService.update(studentId, student, isUnsafeAllowed));
    }

    @Operation(
            method = "GET",
            summary = "Получение студента по его id",
            parameters = {
                    @Parameter(name = "id", description = "Id студента", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID) // checked
    public StudentDto findById(@PathVariable(name = "id") Long studentId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(studentId));
        return studentDtoMapper.mapToDto(studentService.findByIdOrElseThrow(studentId));
    }

    @Operation(
            method = "DELETE",
            summary = "Удалить студента по его id",
            description = """
                Используется для удаления определенного студента (но не соответствующего ему пользователя!).

                Эта операция может быть выполнена только администратором ресурса.
                """,
            tags = {"ADMIN"},
            parameters = {
                    @Parameter(name = "id", description = "Id студента", in = ParameterIn.PATH),
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(DELETE_STUDENT) // checked
    public void deleteStudent(@PathVariable(value = "id") Long studentId) {
        LOGGER.info("ENTER deleteStudent(%d) endpoint".formatted(studentId));
        studentService.delete(studentId);
    }

    /**
     * Returns list of teams that the student has ever been member of
     * @return new list
     */
    @Operation(
            method = "GET",
            summary = "Найти все команды, в которых когда либо состоял студент",
            parameters = {
                    @Parameter(name = "id", description = "Id студента", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_TEAM_HISTORY) // checked
    public List<TeamDto> getTeamHistory(@PathVariable(value = "id") Long studentId) {
        LOGGER.info("ENTER getTeamHistory(%d) endpoint".formatted(studentId));
        return teamService.getTeamHistoryForStudent(studentId).stream().map(teamDtoMapper::mapToDto).toList();
    }

    @GetMapping(GET_STUDENT_ID_BY_CURRENT_USER)
    public Long getCurrentStudentId() {
        return studentService.getCurrentStudent();
    }
}
