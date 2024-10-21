package ru.sfedu.teamselection.controller.personal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.dto.StudentDto;


@RestController
@RequestMapping()
@Tag(name = "StudentController", description = "API для работы со студентами")
@RequiredArgsConstructor
public class StudentController {
//    private final StudentRepository studentRepository;
//    private final TeamRepository teamRepository;
//    private final UserRepository userRepository;
//    private final TrackRepository trackRepository;
    private static final Logger LOGGER = Logger.getLogger(StudentController.class.getName());

    public static final String FIND_BY_ID = "/api/v1/students/{id}";
    public static final String FIND_BY_LIKE = "/api/v1/students/like";
    public static final String FIND_BY_EMAIL = "/api/v1/students/{email}";

    public static final String SEARCH_STUDENTS = "/api/v1/students/search";
    public static final String FIND_ALL = "/api/v1/students";
    public static final String CREATE_STUDENT = "/api/v1/students";
    public static final String UPDATE_STUDENT = "/api/v1/students/{id}";
    public static final String DELETE_STUDENT = "/api/v1/students/{id}";

    public static final String FIND_SUBSCRIPTIONS_BY_ID = "/api/v1/students/{id}/subscriptions";

    @Operation(
            method = "GET",
            summary = "Поиск студентов с фильтрацией по полям",
            parameters = {
                    @Parameter(name = "input",
                            description = "строка из поиска, разделенная пробелами",
                            in = ParameterIn.PATH),
                    @Parameter(name = "email",
                            description = "email без @sfedu.ru",
                            in = ParameterIn.PATH)
            })
    @GetMapping(FIND_BY_LIKE)
    public List<StudentDto> search(
            @RequestParam(value = "input", required = false) String input,
            @RequestParam(value = "email", required = false) String email
    ) {
        var inputValues = input.split(" ");
        LOGGER.info("ENTER search(%s, %s) endpoint".formatted(input, email));
        throw new NotImplementedException();
    }

    @Operation(
            method = "GET",
            summary = "Получение списка всех студентов за все время"
    )
    @GetMapping(FIND_ALL) // checked
    public List<StudentDto> findAll() {
        LOGGER.info("ENTER findAll() endpoint");
        throw new NotImplementedException();
    }

    @Operation(
            method = "POST",
            summary = "Регистрация пользователя",
            parameters = { @Parameter(name = "student", description = "сущность студента")}
            )
    @PostMapping(CREATE_STUDENT) // checked
    public void createUser(@RequestBody StudentDto student, @PathVariable String type) {
        LOGGER.info("ENTER createUser() endpoint");
        throw new NotImplementedException();
    }

    @Operation(
            method = "POST",
            summary = "Изменить данные пользователя",
            parameters = {
                    @Parameter(name = "id", description = "сущность студента", in = ParameterIn.PATH),
                    //@Parameter(name = "student", description = "сущность студента")
            })
    @PostMapping(UPDATE_STUDENT) // checked
    public void updateStudent(@PathVariable(value = "id") Long studentId,
                                  @RequestBody StudentDto student) {
        LOGGER.info("ENTER updateStudent(%d) endpoint".formatted(studentId));
        throw new NotImplementedException();
    }

    @Operation(
            method = "GET",
            summary = "Найти заявки студентов в различные команды",
            parameters = {
                @Parameter(name = "id", description = "id студента", in = ParameterIn.PATH),
            })
    @GetMapping(FIND_SUBSCRIPTIONS_BY_ID) //checked
    public Object findSubscriptionsById(@PathVariable(value = "id") Long studentId) {
        LOGGER.info("ENTER findSubscriptionsById(%d) endpoint".formatted(studentId));
        throw new NotImplementedException();
    }

    @Operation(
            method = "GET",
            summary = "Получение студента по его id",
            parameters = {
                    @Parameter(name = "id", description = "id студента", in = ParameterIn.PATH),
            }
    )
    @GetMapping(FIND_BY_ID) // checked
    public StudentDto findById(@PathVariable(name = "id") Long studentId) {
        LOGGER.info("ENTER findById(%d) endpoint".formatted(studentId));
        throw new NotImplementedException();
    }

    @Operation(
            method = "DELETE",
            summary = "Удалить студента по его id",
            parameters = {
                    @Parameter(name = "id", description = "id студента", in = ParameterIn.PATH),
            }
    )
    @DeleteMapping(DELETE_STUDENT) // checked
    public void deleteStudent(@PathVariable(value = "id") Long studentId) {
        LOGGER.info("ENTER deleteStudent(%d) endpoint".formatted(studentId));
        throw new NotImplementedException();
    }
}
