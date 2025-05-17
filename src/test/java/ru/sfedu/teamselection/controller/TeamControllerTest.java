package ru.sfedu.teamselection.controller;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import ru.sfedu.teamselection.dto.team.TeamSearchOptionsDto;
import ru.sfedu.teamselection.mapper.student.StudentDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamDtoMapper;
import ru.sfedu.teamselection.service.ApplicationService;
import ru.sfedu.teamselection.service.TeamExportService;
import ru.sfedu.teamselection.service.TeamService;
import ru.sfedu.teamselection.service.UserService;
import ru.sfedu.teamselection.service.security.AzureOidcUserService;
import ru.sfedu.teamselection.service.security.Oauth2UserService;

/**
 * Test class for the {@link TeamController}
 */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest({TeamController.class})
public class TeamControllerTest {
    @MockitoBean
    private TeamExportService teamExportService;
    @MockitoBean
    private TeamService teamService;
    @MockitoBean
    private ApplicationService applicationService;
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

    private final Team genericTeam = Team.builder()
            .build();

    private final List<Team> teams = List.of(
            genericTeam
    );

    @BeforeEach
    public void beforeEach() {
        Mockito.doReturn(genericStudentUser)
                .when(userService).getCurrentUser();
    }

    @Test
    public void getSearchOptionsTeams() throws Exception {
        Mockito.doReturn(TeamSearchOptionsDto.builder().build())
                .when(teamService)
                .getSearchOptionsTeams(Mockito.anyLong());

        mockMvc.perform(get(TeamController.GET_SEARCH_OPTIONS + "?track_id=1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void findAll() throws Exception {
        Mockito.doReturn(teams).when(teamService).findAll();

        mockMvc.perform(get(TeamController.FIND_ALL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void search() throws Exception {
        Mockito.doReturn(teams).when(teamService).search(
                Mockito.anyString(),
                Mockito.anyLong(),
                Mockito.anyBoolean(),
                Mockito.anyString(),
                Mockito.anyList()
        );

        mockMvc.perform(get(TeamController.SEARCH_TEAMS)
                        .param("input", "")
                        .param("track_id", "0")
                        .param("is_full", "false")
                        .param("project_type", "")
                        .param("technologies", "")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteTeamFromNonAdminShouldFail() throws Exception {
        Mockito.doNothing().when(teamService).delete(Mockito.notNull());

        mockMvc.perform(delete(StudentController.DELETE_STUDENT, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void deleteTeam() throws Exception {
        Mockito.doNothing().when(teamService).delete(Mockito.notNull());

        mockMvc.perform(delete(TeamController.DELETE_TEAM, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin)))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    public void deleteTeamNotFromAdminShouldFail() throws Exception {
        Mockito.doNothing().when(teamService).delete(Mockito.notNull());

        mockMvc.perform(delete(TeamController.DELETE_TEAM, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void findById() throws Exception {
        Mockito.doReturn(genericTeam).when(teamService).findByIdOrElseThrow(Mockito.anyLong());

        mockMvc.perform(get(TeamController.FIND_BY_ID, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void findApplicantsById() throws Exception {

        mockMvc.perform(get(TeamController.FIND_APPLICANTS_BY_ID, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createTeam() throws Exception {
        Mockito.doReturn(genericTeam).when(teamService).create(Mockito.notNull());

        String team = """
                {
                    "name": "",
                    "projectDescription": "",
                    "projectType": {},
                    "captainId": 0,
                    "technologies": [],
                    "currentTrackId": 0
                }""";

        mockMvc.perform(post(TeamController.CREATE_TEAM)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(team)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void addStudentToTeam() throws Exception {
        Mockito.doReturn(genericTeam).when(teamService).addStudentToTeam(
                Mockito.anyLong(),
                Mockito.anyLong()
        );

        mockMvc.perform(put(TeamController.ADD_STUDENT_TO_TEAM, "1", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void addStudentToTeamNotFromAdminShouldFail() throws Exception {
        Mockito.doReturn(genericTeam).when(teamService).addStudentToTeam(
                Mockito.anyLong(),
                Mockito.anyLong()
        );

        mockMvc.perform(put(TeamController.ADD_STUDENT_TO_TEAM, "1", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void updateTeam() throws Exception {
        Mockito.doReturn(genericTeam).when(teamService).update(
                Mockito.anyLong(),
                Mockito.notNull(),
                Mockito.notNull()
        );

        String team = """
                {
                    "id": 1,
                    "name": "name",
                    "projectDescription": "projectDescription",
                    "projectType": {},
                    "quantityOfStudents": 0,
                    "captain": {
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
                    },
                    "isFull": false,
                    "currentTrackId": 0,
                    "students": [],
                    "applications": [],
                    "technologies": []
                }""";

        mockMvc.perform(put(TeamController.UPDATE_TEAM, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(team)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
