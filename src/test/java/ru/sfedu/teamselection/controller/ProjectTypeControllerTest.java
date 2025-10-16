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
import ru.sfedu.teamselection.domain.ProjectType;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.ProjectTypeDto;
import ru.sfedu.teamselection.exception.CustomExceptionHandler;
import ru.sfedu.teamselection.mapper.ProjectTypeMapper;
import ru.sfedu.teamselection.repository.ProjectTypeRepository;
import ru.sfedu.teamselection.service.audit.AuditService;
import ru.sfedu.teamselection.service.security.AzureOidcUserService;
import ru.sfedu.teamselection.service.security.Oauth2UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link ProjectTypeController}
 */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest({ProjectTypeController.class, CustomExceptionHandler.class})
public class ProjectTypeControllerTest {
    @MockitoBean
    private SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;
    @MockitoBean
    private Oauth2UserService oauth2UserService;
    @MockitoBean
    private AzureOidcUserService azureOidcUserService;

    @MockitoBean
    private AuditService auditService;

    @MockitoBean
    private ProjectTypeRepository projectTypeRepository;
    @MockitoBean
    private ProjectTypeMapper projectTypeMapper;

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

    private final List<ProjectType> projectTypeList = List.of(
            ProjectType.builder().id(1L).name("1").build(),
            ProjectType.builder().id(1L).name("1").build(),
            ProjectType.builder().id(1L).name("1").build()
    );

    private final List<ProjectTypeDto> projectTypeDtoList = List.of(
            new ProjectTypeDto().id(1L).name("1"),
            new ProjectTypeDto().id(2L).name("2"),
            new ProjectTypeDto().id(3L).name("3")
    );

    @BeforeEach
    public void setup() {
        Mockito.doReturn(projectTypeDtoList)
                .when(projectTypeMapper)
                .mapListToDto(projectTypeList);
        Mockito.doReturn(projectTypeList)
                .when(projectTypeMapper)
                .mapListToEntity(projectTypeDtoList);
        Mockito.doReturn(projectTypeList.get(0))
                .when(projectTypeMapper)
                .mapToEntity(Mockito.notNull());
        Mockito.doReturn(projectTypeDtoList.get(0))
                .when(projectTypeMapper)
                .mapToDto(Mockito.notNull());
        Mockito.doReturn(projectTypeDtoList.get(0))
                .when(projectTypeRepository)
                .save(Mockito.notNull());

    }

    @Test
    public void findAll() throws Exception {
        mockMvc.perform(get("/api/v1/projectTypes")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void createProjectTypeNotFromAdminShouldFail() throws Exception {
        String projectType = """
                {
                    "name": "abc"
                }""";

        mockMvc.perform(post("/api/v1/projectTypes")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(projectType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createProjectType() throws Exception {
        Mockito.doReturn(
                ProjectType.builder()
                        .id(101L)
                        .name("abc")
                        .build()
        ).when(projectTypeRepository).save(Mockito.any());

        String projectType = """
                {
                    "name": "abc"
                }""";

        mockMvc.perform(post("/api/v1/projectTypes")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(projectType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNonExistingProjectTypeFromAdmin() throws Exception {
        Mockito.doReturn(false).when(projectTypeRepository).existsById(2L);
        Mockito.doNothing().when(projectTypeRepository).delete(Mockito.any());

        mockMvc.perform(delete("/api/v1/projectTypes/{id}".replace("{id}", "2"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteProjectTypeFromAdmin() throws Exception {
        Mockito.doReturn(true).when(projectTypeRepository).existsById(2L);
        Mockito.doNothing().when(projectTypeRepository).delete(Mockito.any());

        mockMvc.perform(delete("/api/v1/projectTypes/{id}".replace("{id}", "2"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteProjectTypeFromGenericUserShouldFail() throws Exception {
        Mockito.doNothing().when(projectTypeRepository).delete(Mockito.any());

        mockMvc.perform(delete("/api/v1/projectTypes/{id}".replace("{id}", "2"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
