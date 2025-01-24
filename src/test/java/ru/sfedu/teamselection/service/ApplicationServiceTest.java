package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.sfedu.teamselection.BasicTestContainerTest;
import ru.sfedu.teamselection.TeamSelectionApplication;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.repository.ApplicationRepository;
import ru.sfedu.teamselection.repository.UserRepository;

@SpringBootTest(classes = TeamSelectionApplication.class)
@Transactional
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
class ApplicationServiceTest extends BasicTestContainerTest {
    @Autowired
    private ApplicationService underTest;

    @Autowired
    @SpyBean
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

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

    /**
     * Trying to create application for the wrong track.
     * Master student should not be able to create application for bachelor's team
     */
    @Test
    void createForInappropriateTrackShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(18L)
                .teamId(1L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.create(dto, userRepository.findById(17L).orElseThrow())
        );
    }

    @Test
    void createForAnotherUserShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(18L)
                .teamId(1L)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.create(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    void createIfHasTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
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
                .status(ApplicationStatus.SENT)
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
                .status(ApplicationStatus.SENT)
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
                .status(ApplicationStatus.SENT)
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
    @Sql(value = {"/sql-scripts/create_almost_full_team_without_1_second_year.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void createForSecondYear() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(6L)
                .teamId(5L)
                .build();

        Application expected = Application.builder()
                .status("sent")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.create(dto, userRepository.findById(7L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void createWithIdShouldUpdate() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status(ApplicationStatus.REJECTED)
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
                .status(ApplicationStatus.SENT)
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
                .status(ApplicationStatus.REJECTED)
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
                .status(ApplicationStatus.REJECTED)
                .studentId(1L)
                .teamId(1L)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.create(dto, userRepository.findById(4L).orElseThrow())
        );
    }

    @Test
    void updateReject() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status(ApplicationStatus.REJECTED)
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
                .status(ApplicationStatus.ACCEPTED)
                .studentId(1L)
                .teamId(1L)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(4L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_team_for_history.sql"},
            statements = """
                    UPDATE students
                    SET current_team_id = null, has_team = false
                    WHERE id = 2;
                    """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateAcceptFromCaptainWhoHasNoTeamNowShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1004L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(9L)
                .teamId(1004L)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_team_for_history.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateAcceptFromCaptainWhoIsNowMemberOfAnotherTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1004L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(9L)
                .teamId(1004L)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    void updateAccept() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(5L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(5L)
                .teamId(1L)
                .build();

        Application expected = Application.builder()
                .status("accepted")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(3L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());

        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        // student has team now
        Assertions.assertTrue(actual.getStudent().getHasTeam());
        Assertions.assertEquals(dto.getTeamId(), actual.getStudent().getCurrentTeam().getId());

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_almost_full_team_without_1_second_year.sql",
            "/sql-scripts/create_application_from_second_year.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateAcceptForSecondYear() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(101L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(6L)
                .teamId(5L)
                .build();

        Application expected = Application.builder()
                .status("accepted")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(22L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());

        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        // student has team now
        Assertions.assertTrue(actual.getStudent().getHasTeam());
        Assertions.assertEquals(dto.getTeamId(), actual.getStudent().getCurrentTeam().getId());

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_full_team_with_captain.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateAcceptForFullTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1004L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(9L)
                .teamId(1004L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.update(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_team_full_of_second_year.sql",
            "/sql-scripts/create_application_for_team_full_of_second_years.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateAcceptFromSecondYearForTeamFullOfSecondYearsShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(101L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(6L)
                .teamId(5L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.update(dto, userRepository.findById(22L).orElseThrow())
        );
    }

    @Test
    void updateCancelFromForeignUserShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(6L)
                .teamId(2L)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(17L).orElseThrow())
        );
    }

    @Test
    void updateCancelAllowedOnlyForSentApplication() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(9L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(16L)
                .teamId(3L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.update(dto, userRepository.findById(15L).orElseThrow())
        );
    }

    @Test
    void updateSentShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(4L)
                .status(ApplicationStatus.SENT)
                .studentId(1L)
                .teamId(3L)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.update(dto, userRepository.findById(15L).orElseThrow())
        );
    }

    @Test
    void updateCancel() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(6L)
                .teamId(2L)
                .build();

        Application expected = Application.builder()
                .status("cancelled")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(7L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());

        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        // student has team now

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void findTeamApplications() {
        Long teamId = 4L;

        var actual = underTest.findTeamApplications(teamId);

        Assertions.assertEquals(2, actual.size());
        var resultedStudentIds = actual.stream().map(Student::getId).sorted().toList();
        Assertions.assertEquals(List.of(16L, 19L), resultedStudentIds);
    }

    @Test
    @Sql(value = "/sql-scripts/create_team_for_history.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findTeamApplicationsForTeamWithoutApplications() {
        Long teamId = 1003L;

        var actual = underTest.findTeamApplications(teamId);

        Assertions.assertEquals(0, actual.size());
    }

    @Test
    void delete() {
        Long applicationId = 1L;
        underTest.delete(applicationId);

        Assertions.assertFalse(applicationRepository.existsById(applicationId));
    }
}