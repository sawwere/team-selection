package ru.sfedu.teamselection.controller;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.sfedu.teamselection.config.SecurityConfig;
import ru.sfedu.teamselection.config.security.SimpleAuthenticationSuccessHandler;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.exception.CustomExceptionHandler;
import ru.sfedu.teamselection.mapper.TechnologyDtoMapper;
import ru.sfedu.teamselection.repository.TechnologyRepository;
import ru.sfedu.teamselection.service.security.AzureOidcUserService;
import ru.sfedu.teamselection.service.security.Oauth2UserService;

/**
 * Test class for the {@link TechnologyController}
 */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest({TechnologyController.class, CustomExceptionHandler.class})
public class TechnologyControllerTest {
    @MockBean
    private SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;
    @MockBean
    private Oauth2UserService oauth2UserService;
    @MockBean
    private AzureOidcUserService azureOidcUserService;

    @MockBean
    private TechnologyRepository technologyRepository;
    @MockBean
    private TechnologyDtoMapper technologyDtoMapper;

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

    private final List<Technology> technologyList = List.of(
            new Technology(1L, "1"),
            new Technology(2L, "2"),
            new Technology(3L, "3")
    );

    private final List<TechnologyDto> technologyDtoList = List.of(
            new TechnologyDto(1L, "1"),
            new TechnologyDto(2L, "2"),
            new TechnologyDto(3L, "3")
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
        Mockito.doReturn(technologyList.get(0))
                .when(technologyRepository)
                .save(Mockito.notNull());

    }

    @Test
    public void findAll() throws Exception {
        mockMvc.perform(get(TechnologyController.FIND_ALL)
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

        mockMvc.perform(post(TechnologyController.CREATE_TECHNOLOGY)
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

        mockMvc.perform(post(TechnologyController.CREATE_TECHNOLOGY)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(technology)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
