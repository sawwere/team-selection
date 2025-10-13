package ru.sfedu.teamselection.controller;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.exception.CustomExceptionHandler;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.service.TechnologyService;
import ru.sfedu.teamselection.service.audit.AuditService;
import ru.sfedu.teamselection.service.security.AzureOidcUserService;
import ru.sfedu.teamselection.service.security.Oauth2UserService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link TechnologyController}
 */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest({TechnologyController.class, CustomExceptionHandler.class})
public class TechnologyControllerTest {
    @MockitoBean
    private SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;
    @MockitoBean
    private Oauth2UserService oauth2UserService;
    @MockitoBean
    private AzureOidcUserService azureOidcUserService;

    @MockitoBean
    private AuditService auditService;

    @MockitoBean
    private TechnologyService technologyService;
    @MockitoBean
    private TechnologyMapper technologyDtoMapper;

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

    private final List<Technology> technologyList = List.of(
            Technology.builder().id(1L).name("1").build(),
            Technology.builder().id(1L).name("1").build(),
            Technology.builder().id(1L).name("1").build()
    );

    private final List<TechnologyDto> technologyDtoList = List.of(
            new TechnologyDto().id(1L).name("1"),
            new TechnologyDto().id(2L).name("2"),
            new TechnologyDto().id(3L).name("3")
            );

    @BeforeEach
    public void setup() {
        Mockito.doReturn(technologyDtoList)
                .when(technologyDtoMapper)
                .mapListToDto(technologyList);
        Mockito.doReturn(technologyList)
                .when(technologyDtoMapper)
                .mapListToEntity(technologyDtoList);
        Mockito.doReturn(technologyList.get(0))
                .when(technologyDtoMapper)
                .mapToEntity(Mockito.notNull());
        Mockito.doReturn(technologyDtoList.get(0))
                .when(technologyDtoMapper)
                .mapToDto(Mockito.notNull());
        Mockito.doReturn(technologyDtoList.get(0))
                .when(technologyService)
                .create(Mockito.notNull());

    }

    @Test
    public void findAll() throws Exception {
        mockMvc.perform(get("/api/v1/technologies")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void createTechnologyNotFromAdminShouldFail() throws Exception {
        String technology = """
                {
                    "id": 1,
                    "name": "abc"
                }""";

        mockMvc.perform(post("/api/v1/technologies")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(technology)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createTechnology() throws Exception {
        String technology = """
                {
                    "id": 101,
                    "name": "abc"
                }""";

        mockMvc.perform(post("/api/v1/technologies")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(technology)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteTechnologyFromAdmin() throws Exception {
        Mockito.doNothing().when(technologyService).delete(Mockito.any());

        mockMvc.perform(delete("/api/v1/technologies/{id}".replace("{id}", "2"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteTechnologyFromGenericUserShouldFail() throws Exception {
        Mockito.doNothing().when(technologyService).delete(Mockito.any());

        mockMvc.perform(delete("/api/v1/technologies/{id}".replace("{id}", "2"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
