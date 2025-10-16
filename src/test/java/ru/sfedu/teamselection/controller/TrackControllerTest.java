package ru.sfedu.teamselection.controller;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.sfedu.teamselection.config.SecurityConfig;
import ru.sfedu.teamselection.config.security.SimpleAuthenticationSuccessHandler;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.track.TrackDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.exception.CustomExceptionHandler;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.track.TrackDtoMapper;
import ru.sfedu.teamselection.service.TrackService;
import ru.sfedu.teamselection.service.audit.AuditService;
import ru.sfedu.teamselection.service.security.AzureOidcUserService;
import ru.sfedu.teamselection.service.security.Oauth2UserService;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link TrackController}
 */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest({TrackController.class, CustomExceptionHandler.class})
public class TrackControllerTest {
    @MockitoBean
    private SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;
    @MockitoBean
    private Oauth2UserService oauth2UserService;
    @MockitoBean
    private AzureOidcUserService azureOidcUserService;

    @MockitoBean
    private AuditService auditService;

    @MockitoBean
    private TrackService trackService;
    @MockitoBean
    private TrackDtoMapper TrackDtoMapper;

    @Autowired
    private MockMvc mockMvc;

    private final User admin = User.builder()
            .id(1L)
            .fio("admin")
            .email("admin@.com")
            .isEnabled(true)
            .isRemindEnabled(true)
            .role(Role.builder().id(3L).name("ROLE_ADMIN").build())
            .build();

    private final User genericStudentUser = User.builder()
            .id(2L)
            .fio("A B C")
            .email("example@.com")
            .isEnabled(true)
            .isRemindEnabled(true)
            .role(Role.builder().id(1L).name("ROLE_STUDENT").build())
            .build();

    private final List<Track> trackList = List.of(
            Track.builder()
                    .id(1L)
                    .name("1")
                    .about("some text about")
                    .startDate(LocalDate.of(2025, 9, 12))
                    .endDate(LocalDate.of(2026, 9, 11))
                    .type(TrackType.bachelor)
                    .maxConstraint(7)
                    .minConstraint(2)
                    .maxSecondCourseConstraint(2)
                    .build(),
            Track.builder()
                    .id(2L)
                    .name("2")
                    .about("some text about 2")
                    .startDate(LocalDate.of(2025, 9, 12))
                    .endDate(LocalDate.of(2026, 9, 11))
                    .type(TrackType.master)
                    .maxConstraint(7)
                    .minConstraint(2)
                    .maxSecondCourseConstraint(2)
                    .build()
    );

    @Test
    public void findAll() throws Exception {
        Mockito.doReturn(List.of(
                TrackDto.builder()
                        .id(2L)
                        .name("2")
                        .about("some text about 2")
                        .startDate(LocalDate.of(2025, 9, 12))
                        .endDate(LocalDate.of(2026, 9, 11))
                        .type("master")
                        .maxConstraint(7)
                        .minConstraint(2)
                        .maxSecondCourseConstraint(2)
                        .build()
                )
        ).when(trackService).findAll();

        mockMvc.perform(get("/api/v1/tracks")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void whenFindByExistingIdThenReturn200() throws Exception {
        Mockito.doReturn(trackList.get(0)).when(trackService).findByIdOrElseThrow(2L);

        mockMvc.perform(get("/api/v1/tracks/{id}".replace("{id}", "2"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void whenFindByNotExistingIdThenReturn404() throws Exception {
        Mockito.doThrow(new NotFoundException("text")).when(trackService).findByIdOrElseThrow(anyLong());

        mockMvc.perform(get("/api/v1/tracks/{id}".replace("{id}", "6000"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenCreateTrackFromAdminThenReturn200() throws Exception {
        String track = """
                {
                   "name": "Spring Boot Fundamentals",
                   "about": "A comprehensive track covering Spring Boot basics",
                   "startDate": "2024-01-15",
                   "endDate": "2024-03-15",
                   "type": "BOOTCAMP",
                   "minConstraint": 5,
                   "maxConstraint": 20,
                   "maxSecondCourseConstraint": 10
                 }""";

        mockMvc.perform(post("/api/v1/tracks")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(track)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenCreateTrackNotFromAdminThenReturn403() throws Exception {
        String track = """
                {
                   "name": "Spring Boot Fundamentals",
                   "about": "A comprehensive track covering Spring Boot basics",
                   "startDate": "2024-01-15",
                   "endDate": "2024-03-15",
                   "type": "BOOTCAMP",
                   "minConstraint": 5,
                   "maxConstraint": 20,
                   "maxSecondCourseConstraint": 10
                 }""";

        mockMvc.perform(post("/api/v1/tracks")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(track)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUpdateTrackFromAdminThenReturn200() throws Exception {
        Mockito.doReturn(trackList.get(0)).when(trackService).update(Mockito.eq(1L), Mockito.any());
        String track = """
                {
                   "id": 1,
                   "name": "Spring Boot Fundamentals",
                   "about": "A comprehensive track covering Spring Boot basics",
                   "startDate": "2024-01-15",
                   "endDate": "2024-03-15",
                   "type": "BOOTCAMP",
                   "minConstraint": 5,
                   "maxConstraint": 20,
                   "maxSecondCourseConstraint": 10
                 }""";

        mockMvc.perform(put("/api/v1/tracks/{id}".replace("{id}", "1"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(track)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenUpdateTrackNotFromAdminThenReturn403() throws Exception {
        Mockito.doReturn(trackList.get(0)).when(trackService).update(Mockito.eq(1L), Mockito.any());
        String track = """
                {
                   "id": 1,
                   "name": "Spring Boot Fundamentals",
                   "about": "A comprehensive track covering Spring Boot basics",
                   "startDate": "2024-01-15",
                   "endDate": "2024-03-15",
                   "type": "BOOTCAMP",
                   "minConstraint": 5,
                   "maxConstraint": 20,
                   "maxSecondCourseConstraint": 10
                 }""";

        mockMvc.perform(put("/api/v1/tracks/{id}".replace("{id}", "1"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(track)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteTrackFromAdmin() throws Exception {
        Mockito.doNothing().when(trackService).deleteById(Mockito.any());

        mockMvc.perform(delete("/api/v1/tracks/{trackId}".replace("{trackId}", "1"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTrackFromGenericUserShouldFail() throws Exception {
        Mockito.doNothing().when(trackService).deleteById(Mockito.any());

        mockMvc.perform(delete("/api/v1/tracks/{id}".replace("{id}", "2"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
