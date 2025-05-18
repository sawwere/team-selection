package ru.sfedu.teamselection.controller;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.sfedu.teamselection.config.SecurityConfig;
import ru.sfedu.teamselection.config.security.SimpleAuthenticationSuccessHandler;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.student.StudentSearchOptionsDto;
import ru.sfedu.teamselection.mapper.PageResponseMapper;
import ru.sfedu.teamselection.mapper.student.StudentDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamDtoMapper;
import ru.sfedu.teamselection.service.StudentExportService;
import ru.sfedu.teamselection.service.StudentService;
import ru.sfedu.teamselection.service.TeamService;
import ru.sfedu.teamselection.service.UserService;
import ru.sfedu.teamselection.service.security.AzureOidcUserService;
import ru.sfedu.teamselection.service.security.Oauth2UserService;

/**
 * Test class for the {@link StudentController}
 */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest({StudentController.class})
public class StudentControllerTest {
    @MockitoBean
    private TeamService teamService;
    @MockitoBean
    private StudentExportService studentExportService;
    @MockitoBean(name = "studentService")
    private StudentService studentService;
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;
    @MockitoBean
    private Oauth2UserService oauth2UserService;
    @MockitoBean
    private AzureOidcUserService azureOidcUserService;

    @MockitoBean
    private TeamDtoMapper teamDtoMapper;
    @MockitoBean
    private StudentDtoMapper studentDtoMapper;
    @MockitoBean
    private PageResponseMapper pageResponseMapper;

    @Autowired
    private MockMvc mockMvc;

    private final User admin = User.builder()
            .id(1L)
            .fio("admin")
            .email("admin@.com")
            .isEnabled(true)
            .isRemindEnabled(true)
            .role(Role.builder().id(3L).name("ADMIN").build())
            .build();

    private final User genericStudentUser = User.builder()
            .id(2L)
            .fio("A B C")
            .email("example@.com")
            .isEnabled(true)
            .isRemindEnabled(true)
            .role(Role.builder().id(1L).name("STUDENT").build())
            .build();

    private final Student genericStudent = Student.builder()
            .id(666L)
            .course(1)
            .groupNumber(11)
            .aboutSelf("about_self")
            .contacts("dto.getContacts()")
            .currentTeam(Team.builder()
                    .id(1L)
                    .build())
            .user(genericStudentUser)
            .build();

    private final List<Student> students = List.of(
            genericStudent,
            Student.builder().build()
    );

    @BeforeEach
    public void beforeEach() {
        Mockito.doReturn(genericStudentUser)
                .when(userService).getCurrentUser();
    }

    @Test
    public void getSearchOptionsStudents() throws Exception {
        Mockito.doReturn(StudentSearchOptionsDto.builder().build())
                .when(studentService)
                .getSearchOptionsStudents();

        mockMvc.perform(get(StudentController.GET_SEARCH_OPTIONS)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void search() throws Exception {
        Mockito.doReturn(new PageImpl<>(students)).when(studentService).search(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        );

        mockMvc.perform(get(StudentController.SEARCH_STUDENTS)
                        .param("input", "")
                        .param("course", "")
                        .param("group_number", "")
                        .param("has_team", "false")
                        .param("is_captain", "false")
                        .param("technologies", "")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin)))
                .andExpect(status().isOk());
    }

    @Test
    public void findAll() throws Exception {
        Mockito.doReturn(students).when(studentService).findAll();

        mockMvc.perform(get(StudentController.FIND_ALL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void createStudent() throws Exception {
        Mockito.doReturn(genericStudent).when(studentService).create(Mockito.notNull());

        String student = """
                {
                    "course": -1,
                    "groupNumber": 2,
                    "aboutSelf": "о себе",
                    "contacts": "телефонный номер",
                    "userId": 2
                }""";

        mockMvc.perform(post(StudentController.CREATE_STUDENT)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(student)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateStudentFromAdmin() throws Exception {
        Mockito.doReturn(null).when(studentService).getCurrentStudent();
        Mockito.doReturn(genericStudent)
                .when(studentService)
                .update(Mockito.notNull(), Mockito.notNull(), Mockito.anyBoolean());

        String student = """
                {
                    "id": 1,
                    "course": 0,
                    "groupNumber": 0,
                    "aboutSelf": "",
                    "contacts": "",
                    "hasTeam": false,
                    "isCaptain": false,
                    "currentTeam": {},
                    "technologies": [],
                    "applications": [],
                    "user": {}
                }""";

        mockMvc.perform(put(StudentController.UPDATE_STUDENT, genericStudent.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(student)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateStudentFromStudentThemselves() throws Exception {
        Mockito.doReturn(genericStudent.getId()).when(studentService).getCurrentStudent();
        Mockito.doReturn(genericStudent)
                .when(studentService)
                .update(Mockito.notNull(), Mockito.notNull(), Mockito.notNull());

        String student = """
                {
                    "id": 1,
                    "course": 0,
                    "groupNumber": 0,
                    "aboutSelf": "",
                    "contacts": "",
                    "hasTeam": false,
                    "isCaptain": false,
                    "currentTeam": {},
                    "technologies": [],
                    "applications": [],
                    "user": {}
                }""";

        mockMvc.perform(put(StudentController.UPDATE_STUDENT, genericStudent.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(student)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateStudentFromForeignStudentShouldFail() throws Exception {
        Mockito.doReturn(genericStudent.getId()).when(studentService).getCurrentStudent();
        Mockito.doReturn(genericStudent)
                .when(studentService)
                .update(Mockito.notNull(), Mockito.notNull(), Mockito.notNull());

        String student = """
                {
                    "id": 1,
                    "course": 0,
                    "groupNumber": 0,
                    "aboutSelf": "",
                    "contacts": "",
                    "hasTeam": false,
                    "isCaptain": false,
                    "currentTeam": {},
                    "technologies": [],
                    "applications": [],
                    "user": {}
                }""";

        mockMvc.perform(put(StudentController.UPDATE_STUDENT, genericStudent.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(student)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findById() throws Exception {
        mockMvc.perform(get(StudentController.FIND_BY_ID, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteStudentFromNonAdminShouldFail() throws Exception {
        Mockito.doNothing().when(studentService).delete(Mockito.notNull());

        mockMvc.perform(delete(StudentController.DELETE_STUDENT, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void deleteStudent() throws Exception {
        Mockito.doNothing().when(studentService).delete(Mockito.any());

        mockMvc.perform(delete(StudentController.DELETE_STUDENT, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin)))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    public void deleteStudentNotFromAdminShouldFail() throws Exception {
        Mockito.doNothing().when(studentService).delete(Mockito.notNull());

        mockMvc.perform(delete(StudentController.DELETE_STUDENT, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void getTeamHistory() throws Exception {
        mockMvc.perform(get(StudentController.FIND_TEAM_HISTORY, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void getCurrentStudentId() throws Exception {
        Mockito.doReturn(genericStudent.getId()).when(studentService).getCurrentStudent();

        mockMvc.perform(get(StudentController.GET_STUDENT_ID_BY_CURRENT_USER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
