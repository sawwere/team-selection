package ru.sfedu.teamselection.service;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.BasicTestContainerTest;
import ru.sfedu.teamselection.TeamSelectionApplication;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.dto.student.StudentSummaryDto;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.UserRepository;
import ru.sfedu.teamselection.service.security.PermissionLevelUpdate;

@SpringBootTest(classes = TeamSelectionApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
    void whenFindByIdExistingThenReturnUserWithSuchId() {
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
    void whenFindByIdNonExistingThenThrow() {
        // given
        // when + then
        var ex = Assertions.assertThrows(NotFoundException.class, () -> underTest.findByIdOrElseThrow(-1L));
        Assertions.assertTrue(ex.getMessage().contains("Пользователь"));
    }

    @Test
    void whenFindByEmailExistingThenReturnUserWithSuchEmail() {
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
    void whenFindByEmailNonExistingThenThrow() {
        // given
        // when + then
        var ex = Assertions.assertThrows(NotFoundException.class, () -> underTest.findByEmail("nonexistennt@mail"));
        Assertions.assertTrue(ex.getMessage().contains("Пользователь"));
    }

    @Test
    void whenFindByUsernameExistingThenReturnUserWithSuchUsername() {
        // given
        User expected = User.builder()
                .id(1L)
                .fio("admin")
                .email("admin_mail")
                .role(Role.builder().id(3L).name("ADMIN").build())
                .build();
        // when
        User actual = underTest.findByUsername(expected.getFio());
        // then
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFio(), actual.getFio());
    }

    @Test
    void whenFindByUsernameNonExistingThenThrow() {
        // given
        // when + then
        var ex = Assertions.assertThrows(NotFoundException.class, () -> underTest.findByUsername("some stupid name"));
        Assertions.assertTrue(ex.getMessage().contains("Пользователь"));
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

        User actual = underTest.createOrUpdate(dto, PermissionLevelUpdate.OWNER);

        Assertions.assertEquals(expected.getFio(), actual.getFio());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getRole().getName(), actual.getRole().getName());
    }

    @Test
    void updateUser() {
        UserDto dto = UserDto.builder()
                .id(101L)
                .fio("n e w")
                .email("mail@m")
                .role("USER")
                .isRemindEnabled(false)
                .isEnabled(false)
                .student(
                        StudentSummaryDto.builder()
                                .course(1)
                                .groupNumber(2)
                                .currentTrackId(3L)
                                .build()
                )
                .build();

        User expected = User.builder()
                .id(dto.getId())
                .fio("Васильева Екатерина Петровна")
                .email("user26@_mail")
                .role(Role.builder().id(1L).name("USER").build())
                .isRemindEnabled(dto.getIsRemindEnabled())
                .isEnabled(true)
                .student(null)
                .build();

        User actual = underTest.createOrUpdate(dto, PermissionLevelUpdate.OWNER);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getRole().getName(), actual.getRole().getName());
        Assertions.assertEquals(expected.getIsRemindEnabled(), actual.getIsRemindEnabled());
        Assertions.assertEquals(expected.getIsEnabled(), actual.getIsEnabled());
        Assertions.assertNull(expected.getStudent());
    }

    @Test
    void updateUserStudent() {
        UserDto dto = UserDto.builder()
                .id(105L)
                .fio("n e w")
                .email("mail@m")
                .role("USER")
                .isRemindEnabled(false)
                .isEnabled(false)
                .student(
                        StudentSummaryDto.builder()
                                .course(1)
                                .groupNumber(12)
                                .currentTrackId(3L)
                                .build()
                )
                .build();

        User expected = User.builder()
                .id(dto.getId())
                .fio(dto.getFio())
                .email(dto.getEmail())
                .role(Role.builder().id(1L).name("USER").build())
                .isRemindEnabled(dto.getIsRemindEnabled())
                .isEnabled(dto.getIsEnabled())
                .student(
                        Student.builder()
                                .course(1)
                                .groupNumber(12)
                                .technologies(List.of(
                                        Technology.builder()
                                                .id(1L)
                                                .name("name")
                                                .build()
                                ))
                                .currentTrack(Track.builder().id(3L).build())
                                .build()
                )
                .build();

        User actual = underTest.createOrUpdate(dto, PermissionLevelUpdate.ADMIN);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFio(), actual.getFio());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getRole().getName(), actual.getRole().getName());
        Assertions.assertEquals(expected.getIsRemindEnabled(), actual.getIsRemindEnabled());
        Assertions.assertEquals(expected.getIsEnabled(), actual.getIsEnabled());
        Assertions.assertEquals(expected.getStudent().getCourse(), actual.getStudent().getCourse());
        Assertions.assertEquals(expected.getStudent().getGroupNumber(), actual.getStudent().getGroupNumber());
        Assertions.assertEquals(
                expected.getStudent().getTechnologies().size(),
                actual.getStudent().getTechnologies().size()
        );
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
    void whenAssignStudentRoleTwiceThenDoNotCreateStudentTwice() {
        String roleName = "STUDENT";
        var actual = underTest.assignRole(102L, roleName);
        // do not throw
        actual = underTest.assignRole(102L, roleName);
        Assertions.assertEquals(roleName, actual.getRole().getName());

        // Student should have been created
        Assertions.assertNotNull(studentRepository.findByUserId(actual.getId()));
    }

    @Test
    void assignInvalidRoleShouldFail() {
        String roleName = "role";

        var ex = Assertions.assertThrows(NotFoundException.class, () -> underTest.assignRole(102L, roleName));
        Assertions.assertTrue(ex.getMessage().contains("Роль"));
    }

    @Test
    void whenDeactivateUserThenShouldDisableInDb() {
        // given
        User user = userRepository.findById(102L).orElseThrow();
        // when
        underTest.deactivateUser(102L);
        // then
        Assertions.assertFalse(user.getIsEnabled());
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