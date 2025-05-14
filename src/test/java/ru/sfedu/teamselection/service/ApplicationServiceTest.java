package ru.sfedu.teamselection.service;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.BasicTestContainerTest;
import ru.sfedu.teamselection.TeamSelectionApplication;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.application.ApplicationType;
import ru.sfedu.teamselection.domain.application.TeamInvite;
import ru.sfedu.teamselection.domain.application.TeamRequest;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.exception.BusinessException;
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
    @MockitoSpyBean
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
    void createRequestForInappropriateTrackShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(18L)
                .teamId(1L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(17L).orElseThrow())
        );
    }

    /**
     * Trying to create application for the wrong track.
     * Master student should not be able to create application for bachelor's team
     */
    @Test
    void createInviteForInappropriateTrackShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(9L)
                .teamId(3L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(19L).orElseThrow())
        );
    }

    @Test
    void createRequestForAnotherUserShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(18L)
                .teamId(1L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.create(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    void createInviteForAnotherUserShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(18L)
                .teamId(1L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.create(dto, userRepository.findById(18L).orElseThrow())
        );
    }

    @Test
    void createRequestIfHasTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(4L)
                .teamId(1L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(5L).orElseThrow())
        );
    }

    @Test
    void createInviteIfHasTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(19L)
                .teamId(3L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(19L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {"/sql-scripts/create_team_for_history.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createRequestForFullTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(9L)
                .teamId(1004L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(8L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {"/sql-scripts/create_team_for_history.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createRequestFromSecondYearForTeamWithMaxOfSecondYearsShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(13L)
                .teamId(1003L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(12L).orElseThrow())
        );
    }

    @Test
    void createRequest() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(18L)
                .teamId(3L)
                .type(ApplicationType.REQUEST)
                .build();

        Application expected = TeamRequest.builder()
                .status("SENT")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.create(dto, userRepository.findById(17L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void createInvite() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(18L)
                .teamId(3L)
                .type(ApplicationType.INVITE)
                .build();

        Application expected = TeamInvite.builder()
                .status("SENT")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.create(dto, userRepository.findById(19L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertInstanceOf(TeamInvite.class, actual);
    }

    @Test
    @Sql(value = {"/sql-scripts/create_almost_full_team_without_1_second_year.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void createRequestForSecondYear() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(6L)
                .teamId(5L)
                .type(ApplicationType.REQUEST)
                .build();

        Application expected = TeamRequest.builder()
                .status("SENT")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.create(dto, userRepository.findById(7L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @Sql(value = {"/sql-scripts/create_almost_full_team_without_1_second_year.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void createInviteForSecondYear() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .status(ApplicationStatus.SENT)
                .studentId(6L)
                .teamId(5L)
                .type(ApplicationType.INVITE)
                .build();

        Application expected = TeamInvite.builder()
                .status("SENT")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.create(dto, userRepository.findById(22L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void createRequestWithNonExistentIdShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(666L)
                .status(ApplicationStatus.SENT)
                .studentId(4L)
                .teamId(1L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(5L).orElseThrow())
        );
    }

    @Test
    void createInviteWithNonExistentIdShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(666L)
                .status(ApplicationStatus.SENT)
                .studentId(1L)
                .teamId(1L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    void createWithIdShouldUpdate() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status(ApplicationStatus.REJECTED)
                .studentId(6L)
                .teamId(2L)
                .type(ApplicationType.REQUEST)
                .build();

        Application expected = TeamRequest.builder()
                .status("REJECTED")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.create(dto, userRepository.findById(4L).orElseThrow());

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }


    @Test
    void updateRequestAcceptedShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1L)
                .status(ApplicationStatus.REJECTED)
                .studentId(19L)
                .teamId(4L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(9L).orElseThrow())
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (111, 4, 1, 'accepted', 'invite');
            """,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteAcceptedShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(111L)
                .status(ApplicationStatus.REJECTED)
                .studentId(1L)
                .teamId(4L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.create(dto, userRepository.findById(9L).orElseThrow())
        );
    }

    @Test
    void updateRequestRejectAllowedOnlyFromCaptain() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(4L)
                .status(ApplicationStatus.REJECTED)
                .studentId(1L)
                .teamId(1L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.create(dto, userRepository.findById(4L).orElseThrow())
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (112, 4, 1, 'sent', 'invite');
            """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteRejectAllowedOnlyFromTargetStudent() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(112L)
                .status(ApplicationStatus.REJECTED)
                .studentId(1L)
                .teamId(4L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.create(dto, userRepository.findById(9L).orElseThrow())
        );
    }

    @Test
    void updateRequestReject() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status(ApplicationStatus.REJECTED)
                .studentId(6L)
                .teamId(2L)
                .type(ApplicationType.REQUEST)
                .build();

        Application expected = TeamRequest.builder()
                .status("REJECTED")
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
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (113, 2, 6, 'sent', 'invite');
            """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteReject() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(113L)
                .status(ApplicationStatus.REJECTED)
                .studentId(6L)
                .teamId(2L)
                .type(ApplicationType.INVITE)
                .build();

        Application expected = TeamInvite.builder()
                .status("REJECTED")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(7L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());

        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        // student has no team
        Assertions.assertNull(expected.getStudent().getCurrentTeam());

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void updateRequestAcceptAllowedOnlyFromCaptain() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(4L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(1L)
                .teamId(1L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(4L).orElseThrow())
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (114, 1, 1, 'sent', 'invite');
            """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteAcceptAllowedOnlyFromTargetStudent() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(114L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(1L)
                .teamId(1L)
                .type(ApplicationType.INVITE)
                .build();

        // не должно сработать, если, например, капитан принимает приглашение
        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_team_for_history.sql"
        },
            statements = """
                    UPDATE students
                    SET current_team_id = null, has_team = false
                    WHERE id = 2;
                    """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateRequestAcceptFromCaptainWhoHasNoTeamNowShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1004L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(9L)
                .teamId(1004L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_team_for_history.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateRequestAcceptFromCaptainWhoIsNowMemberOfAnotherTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1004L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(9L)
                .teamId(1004L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    void updateRequestAccept() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(5L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(5L)
                .teamId(1L)
                .type(ApplicationType.REQUEST)
                .build();

        Application expected = TeamRequest.builder()
                .status("ACCEPTED")
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
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (115, 1, 1, 'sent', 'invite');
            """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteAccept() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(115L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(1L)
                .teamId(1L)
                .type(ApplicationType.INVITE)
                .build();

        Application expected = TeamInvite.builder()
                .status("ACCEPTED")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(2L).orElseThrow());

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
    void updateRequestAcceptForSecondYear() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(101L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(6L)
                .teamId(5L)
                .type(ApplicationType.REQUEST)
                .build();

        Application expected = TeamRequest.builder()
                .status("ACCEPTED")
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
            "/sql-scripts/create_almost_full_team_without_1_second_year.sql"
    },
            statements = """
                    INSERT INTO applications
                        (id, team_id, student_id, status, type)
                    VALUES
                        (121, 5, 6, 'sent', 'invite');
                    """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteAcceptForSecondYear() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(121L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(6L)
                .teamId(5L)
                .type(ApplicationType.INVITE)
                .build();

        Application expected = TeamInvite.builder()
                .status("ACCEPTED")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(7L).orElseThrow());

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
    void updateRequestAcceptForFullTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1004L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(9L)
                .teamId(1004L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.update(dto, userRepository.findById(3L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_full_team_with_captain.sql"},
            statements = """
                    INSERT INTO applications
                        (id, team_id, student_id, status, type)
                    VALUES
                        (1014, 1004, 9, 'sent', 'invite');
                    """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteAcceptForFullTeamShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1014L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(9L)
                .teamId(1004L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.update(dto, userRepository.findById(8L).orElseThrow())
        );
    }

    @Test
    @Sql(value = {
            "/sql-scripts/create_team_full_of_second_year.sql",
            "/sql-scripts/create_application_for_team_full_of_second_years.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateRequestAcceptFromSecondYearForTeamFullOfSecondYearsShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(101L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(6L)
                .teamId(5L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.update(dto, userRepository.findById(22L).orElseThrow())
        );
    }

    @Test
    @Sql(value = "/sql-scripts/create_team_full_of_second_year.sql",
            statements = """
                    INSERT INTO applications
                        (id, team_id, student_id, status, type)
                    VALUES
                        (101, 5, 6, 'sent', 'invite');
                    """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteAcceptFromSecondYearForTeamFullOfSecondYearsShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(101L)
                .status(ApplicationStatus.ACCEPTED)
                .studentId(6L)
                .teamId(5L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> underTest.update(dto, userRepository.findById(7L).orElseThrow())
        );
    }

    @Test
    void updateRequestCancelFromNonSenderShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(6L)
                .teamId(2L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(17L).orElseThrow())
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (116, 2, 6, 'sent', 'invite');
            """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteCancelFromNonSenderShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(116L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(6L)
                .teamId(2L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(ForbiddenException.class,
                () -> underTest.update(dto, userRepository.findById(17L).orElseThrow())
        );
    }

    @Test
    void updateRequestCancelAllowedOnlyForSentApplication() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(9L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(16L)
                .teamId(3L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.update(dto, userRepository.findById(15L).orElseThrow())
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (119, 3, 16, 'rejected', 'invite');
            """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteCancelAllowedOnlyForSentApplication() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(119L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(16L)
                .teamId(3L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.update(dto, userRepository.findById(19L).orElseThrow())
        );
    }

    @Test
    void updateRequestSentShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(4L)
                .status(ApplicationStatus.SENT)
                .studentId(1L)
                .teamId(3L)
                .type(ApplicationType.REQUEST)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.update(dto, userRepository.findById(15L).orElseThrow())
        );
    }

    @Test
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (114, 3, 1, 'sent', 'invite');
            """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteSentShouldFail() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(4L)
                .status(ApplicationStatus.SENT)
                .studentId(1L)
                .teamId(3L)
                .type(ApplicationType.INVITE)
                .build();

        Assertions.assertThrows(BusinessException.class,
                () -> underTest.update(dto, userRepository.findById(2L).orElseThrow())
        );
    }

    @Test
    void updateRequestCancel() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(6L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(6L)
                .teamId(2L)
                .type(ApplicationType.REQUEST)
                .build();

        Application expected = TeamRequest.builder()
                .status("CANCELLED")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(7L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());

        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @Sql(statements = """
            INSERT INTO applications
                (id, team_id, student_id, status, type)
            VALUES
                (116, 2, 6, 'sent', 'invite');
            """,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateInviteCancel() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(116L)
                .status(ApplicationStatus.CANCELLED)
                .studentId(6L)
                .teamId(2L)
                .type(ApplicationType.INVITE)
                .build();

        Application expected = TeamInvite.builder()
                .status("CANCELLED")
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .build();

        Application actual = underTest.update(dto, userRepository.findById(4L).orElseThrow());

        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());

        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        // student has team now

        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void findTeamApplicationsStudents() {
        Long teamId = 4L;

        var actual = underTest.findTeamApplicationsStudents(teamId);

        Assertions.assertEquals(2, actual.size());
        var resultedStudentIds = actual.stream().map(Student::getId).sorted().toList();
        Assertions.assertEquals(List.of(16L, 19L), resultedStudentIds);
    }

    @Test
    @Sql(value = "/sql-scripts/create_team_for_history.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findTeamApplicationsForTeamWithoutApplicationsStudents() {
        Long teamId = 1003L;

        var actual = underTest.findTeamApplicationsStudents(teamId);

        Assertions.assertEquals(0, actual.size());
    }

    @Test
    void delete() {
        Long applicationId = 1L;
        underTest.delete(applicationId);

        Assertions.assertFalse(applicationRepository.existsById(applicationId));
    }
}