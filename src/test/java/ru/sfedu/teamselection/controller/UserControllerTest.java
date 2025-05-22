package ru.sfedu.teamselection.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.sfedu.teamselection.config.SecurityConfig;
import ru.sfedu.teamselection.config.security.SimpleAuthenticationSuccessHandler;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.exception.CustomExceptionHandler;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.user.RoleMapper;
import ru.sfedu.teamselection.mapper.user.UserMapper;
import ru.sfedu.teamselection.service.UserService;
import ru.sfedu.teamselection.service.security.AzureOidcUserService;
import ru.sfedu.teamselection.service.security.Oauth2UserService;

/**
 * Test class for the {@link UserController}
 */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest({UserController.class, CustomExceptionHandler.class})
public class UserControllerTest {
    @MockitoBean(name = "userService")
    private UserService userService;

    @MockitoBean
    private SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;
    @MockitoBean
    private Oauth2UserService oauth2UserService;
    @MockitoBean
    private AzureOidcUserService azureOidcUserService;

    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private RoleMapper roleDtoMapper;

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

    private final List<Role> roleDtoList = List.of(
            new Role(1L, "USER"),
            new Role(2L, "JURY"),
            new Role(3L, "ADMIN"),
            new Role(4L, "STUDENT")
    );

    @Test
    public void putUser() throws Exception {
        Mockito.doReturn(genericStudentUser)
                .when(userService).getCurrentUser();
        Mockito.doReturn(genericStudentUser).when(userService).createOrUpdate(Mockito.notNull());

        String userDto = """
                {
                    "id": 2,
                    "fio": "f i o",
                    "email": "email@mail.mail",
                    "role": "USER",
                    "isRemindEnabled": false,
                    "isEnabled": false
                }""";

        mockMvc.perform(put(UserController.PUT_USER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(userDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void putUserFromAdmin() throws Exception {
        Mockito.doReturn(admin)
                .when(userService).getCurrentUser();
        Mockito.doReturn(genericStudentUser).when(userService).createOrUpdate(Mockito.notNull());

        String userDto = """
                {
                    "id": 3,
                    "fio": "f i o",
                    "email": "email@mail.mail",
                    "role": "USER",
                    "isRemindEnabled": false,
                    "isEnabled": false
                }""";

        mockMvc.perform(put(UserController.PUT_USER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(userDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void putUserFromForeignUserShouldFail() throws Exception {
        Mockito.doReturn(genericStudentUser)
                .when(userService).getCurrentUser();
        Mockito.doReturn(genericStudentUser).when(userService).createOrUpdate(Mockito.notNull());

        String userDto = """
                {
                    "id": 3,
                    "fio": "f i o",
                    "email": "email@mail.mail",
                    "role": "USER",
                    "isRemindEnabled": false,
                    "isEnabled": false
                }""";

        mockMvc.perform(put(UserController.PUT_USER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(userDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getCurrentUser() throws Exception {
        Mockito.doReturn(genericStudentUser)
                .when(userService).getCurrentUser();
        
        mockMvc.perform(get(UserController.CURRENT_USER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllRoles() throws Exception {
        Mockito.doReturn(roleDtoList).when(userService).getAllRoles();

        mockMvc.perform(get(UserController.GET_ROLES)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin)))
                .andExpect(status().isOk());
    }

    @Test
    public void assignRole() throws Exception {
        Mockito.doReturn(genericStudentUser).when(userService).assignRole(Mockito.anyLong(), Mockito.anyString());
        
        String roleDto = """
                {
                    "id": 1,
                    "name": "STUDENT"
                }""";

        mockMvc.perform(post(UserController.GRANT_ROLE, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(roleDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void assignRoleNotFromAdminShouldFail() throws Exception {
        Mockito.doReturn(genericStudentUser).when(userService).assignRole(Mockito.anyLong(), Mockito.anyString());

        String roleDto = """
                {
                    "id": 1,
                    "name": "STUDENT"
                }""";

        mockMvc.perform(post(UserController.GRANT_ROLE, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(genericStudentUser))
                        .content(roleDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void assignRoleThrowsOnNonExistingRole() throws Exception {
        Mockito.doThrow(new NotFoundException("Role not found"))
                .when(userService)
                .assignRole(Mockito.anyLong(), Mockito.anyString());

        String roleDto = """
                {
                    "id": 1,
                    "name": "STUDENT"
                }""";

        mockMvc.perform(post(UserController.GRANT_ROLE, "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(admin))
                        .content(roleDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
