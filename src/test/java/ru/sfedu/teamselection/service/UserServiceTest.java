package ru.sfedu.teamselection.service;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.BasicTestContainerTest;
import ru.sfedu.teamselection.TeamSelectionApplication;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.UserRepository;

@SpringBootTest(classes = TeamSelectionApplication.class,
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration")
@Transactional
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
@Sql(value = {"/sql-scripts/create_users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UserServiceTest extends BasicTestContainerTest {
    @Autowired
    private UserService underTest;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
    }

    @Test
    void findByIdOrElseThrow() {
        User expected = User.builder()
                .id(1L)
                .fio("admin")
                .email("admin_mail")
                .role(Role.builder().id(3L).name("ADMIN").build())
                .build();

        User actual = underTest.findByIdOrElseThrow(1L);

        Assertions.assertEquals(expected.getId(), actual.getId());
    }

    @Test
    void findByEmail() {
        User expected = User.builder()
                .id(1L)
                .fio("admin")
                .email("admin_mail")
                .role(Role.builder().id(3L).name("ADMIN").build())
                .build();

        User actual = underTest.findByEmail(expected.getEmail());

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    void findByUsername() {
        User expected = User.builder()
                .id(1L)
                .fio("admin")
                .email("admin_mail")
                .role(Role.builder().id(3L).name("ADMIN").build())
                .build();

        User actual = underTest.findByUsername(expected.getFio());

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFio(), actual.getFio());
    }

    @Test
    void create() {
        UserDto dto = UserDto.builder()
                .fio("n e w")
                .email("mail@m")
                .role("USER")
                .build();

        User expected = User.builder()
                .fio(dto.getFio())
                .email(dto.getEmail())
                .role(Role.builder().id(1L).name("USER").build())
                .build();

        User actual = underTest.createOrUpdate(dto);

        Assertions.assertEquals(expected.getFio(), actual.getFio());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getRole().getName(), actual.getRole().getName());
    }

    @Test
    void update() {
        UserDto dto = UserDto.builder()
                .id(101L)
                .fio("n e w")
                .email("mail@m")
                .role("USER")
                .isRemindEnabled(false)
                .isEnabled(false)
                .build();

        User expected = User.builder()
                .id(dto.getId())
                .fio(dto.getFio())
                .email(dto.getEmail())
                .role(Role.builder().id(1L).name("USER").build())
                .isRemindEnabled(dto.getIsRemindEnabled())
                .isEnabled(dto.getIsEnabled())
                .build();

        User actual = underTest.createOrUpdate(dto);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFio(), actual.getFio());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getRole().getName(), actual.getRole().getName());
        Assertions.assertEquals(expected.getIsRemindEnabled(), actual.getIsRemindEnabled());
        Assertions.assertEquals(expected.getIsEnabled(), actual.getIsEnabled());
    }

    @Test
    void getAllRoles() {
        List<Role> actual = underTest.getAllRoles();

        Assertions.assertEquals(4, actual.size());
    }

    @Test
    void assignRole() {
        String roleName = "JURY";
        var actual = underTest.assignRole(102L, roleName);

        Assertions.assertEquals(roleName, actual.getRole().getName());
    }

    @Test
    void assignStudentRole() {
        String roleName = "STUDENT";
        var actual = underTest.assignRole(102L, roleName);

        Assertions.assertEquals(roleName, actual.getRole().getName());

        // Student should have been created
        Assertions.assertNotNull(studentRepository.findByUserId(actual.getId()));
    }

    @Test
    void assignInvalidRoleShouldFail() {
        String roleName = "role";

        Assertions.assertThrows(RuntimeException.class, () -> underTest.assignRole(102L, roleName));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCurrentStudent() {
        User expected = userRepository.findById(1L).orElseThrow();

        var actual = underTest.getCurrentUser();
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFio(), actual.getFio());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }
}