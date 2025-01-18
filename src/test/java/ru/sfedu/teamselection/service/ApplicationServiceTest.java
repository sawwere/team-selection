package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.BasicTestContainerTest;
import ru.sfedu.teamselection.TeamSelectionApplication;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.repository.ApplicationRepository;
import ru.sfedu.teamselection.repository.TeamRepository;
import ru.sfedu.teamselection.repository.UserRepository;

@SpringBootTest(classes = TeamSelectionApplication.class)
@Transactional
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
class ApplicationServiceTest extends BasicTestContainerTest {
    @Autowired
    private ApplicationService underTest;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;


//    @BeforeEach
//    public void beforeEach() {
//        MockitoAnnotations.openMocks(this);
//
//    }

    @Test
    void findByIdOrElseThrow() {
        Application expected = applicationRepository.findById(1L).orElseThrow();

        Application actual = underTest.findByIdOrElseThrow(1L);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
    }

    @Test
    void findAll() {
        List<Application> expected = applicationRepository.findAll();

        List<Application> actual = underTest.findAll();

        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    void findTeamApplications() {
    }

    /**
     * Trying to create application for the wrong track.
     * Master student should not be able to create application for bachelor's team
     */
    @Test
    void createForInappropriateTrackShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status("sent")
                .studentId(18L)
                .teamId(1L)
                .build();

        Application expected = Application.builder()
                .status("sent")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.create(dto, userRepository.findById(17L).orElseThrow())
        );
    }

    @Test
    void createForAnotherUserShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status("sent")
                .studentId(18L)
                .teamId(1L)
                .build();

        Application expected = Application.builder()
                .status("sent")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Assertions.assertThrows(AccessDeniedException.class,
                () -> underTest.create(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    void createIfHasTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status("sent")
                .studentId(4L)
                .teamId(1L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.create(dto, userRepository.findById(5L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {"/sql-scripts/create_team_for_history.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createForFullTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status("sent")
                .studentId(9L)
                .teamId(1004L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.create(dto, userRepository.findById(8L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {"/sql-scripts/create_team_for_history.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createFromSecondYearForTeamWithMaxOfSecondYearsShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status("sent")
                .studentId(13L)
                .teamId(1003L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.create(dto, userRepository.findById(12L).orElseThrow())
        );
    }

    @Test
    void create() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status("sent")
                .studentId(18L)
                .teamId(3L)
                .build();

        Application expected = Application.builder()
                .status("sent")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.create(dto, userRepository.findById(17L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void creteWithIdShouldUpdate() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status("rejected")
                .studentId(6L)
                .teamId(2L)
                .build();

        Application expected = Application.builder()
                .status("rejected")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.create(dto, userRepository.findById(4L).orElseThrow());


        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void createWithNonExistentIdShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(666L)
                .status("sent")
                .studentId(4L)
                .teamId(1L)
                .build();

        Assertions.assertThrows(NoSuchElementException.class,
                () -> underTest.create(dto, userRepository.findById(5L).orElseThrow())
        );
    }

    @Test
    void updateAcceptedShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1L)
                .status("rejected")
                .studentId(19L)
                .teamId(4L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.create(dto, userRepository.findById(9L).orElseThrow())
        );
    }

    @Test
    void updateRejectAllowedOnlyFromCaptain() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(4L)
                .status("rejected")
                .studentId(1L)
                .teamId(1L)
                .build();

        Application expected = Application.builder()
                .status("rejected")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Assertions.assertThrows(AccessDeniedException.class,
                () -> underTest.create(dto, userRepository.findById(4L).orElseThrow())
        );
    }

    @Test
    void updateReject() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status("rejected")
                .studentId(6L)
                .teamId(2L)
                .build();

        Application expected = Application.builder()
                .status("rejected")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(4L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());

        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        // student has no team
        Assertions.assertNull(expected.getStudent().getCurrentTeam());

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void updateAcceptAllowedOnlyFromCaptain() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(4L)
                .status("accepted")
                .studentId(1L)
                .teamId(1L)
                .build();

        Assertions.assertThrows(AccessDeniedException.class,
                () -> underTest.update(dto, userRepository.findById(4L).orElseThrow())
        );
    }

    @Test
    void updateAccept() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status("accepted")
                .studentId(6L)
                .teamId(2L)
                .build();

        Application expected = Application.builder()
                .status("accepted")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(4L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());

        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        // student has team now
        Assertions.assertTrue(actual.getStudent().getHasTeam());
        Assertions.assertEquals(dto.getTeamId(), actual.getStudent().getCurrentTeam().getId());

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

//    @Test
//    void findApplicationByTeamIdAndStudentId() {
//    }
}