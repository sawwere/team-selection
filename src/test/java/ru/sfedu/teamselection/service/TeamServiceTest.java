package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.dto.student.StudentDto;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.team.ProjectTypeDto;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.dto.team.TeamSearchOptionsDto;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.TeamRepository;


@SpringBootTest(classes = TeamSelectionApplication.class)
@Transactional
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
@Sql(value = {"/sql-scripts/create_team_for_history.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class TeamServiceTest extends BasicTestContainerTest {
    @Autowired
    private TeamService underTest;
    @Autowired
    private UserService userService;

    @Autowired
    @MockitoSpyBean
    private TeamRepository teamRepository;

    @Autowired
    private StudentRepository studentRepository;


    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        Mockito.doNothing().when(teamRepository).delete(Mockito.isNotNull(Team.class));
    }

    @Test
    void findByIdOrElseThrow() {
        Team expected = teamRepository.findById(1L).orElseThrow();

        Team actual = underTest.findByIdOrElseThrow(1L);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getProjectDescription(), actual.getProjectDescription());
        Assertions.assertEquals(expected.getQuantityOfStudents(), actual.getQuantityOfStudents());
    }

    @Test
    void findAll() {
        List<Team> expected = teamRepository.findAll();

        List<Team> actual = underTest.findAll();

        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    void createWhenCaptainHasTeamShouldFail() {
        TeamCreationDto teamCreationDto = TeamCreationDto.builder()
                .name("shouldFail")
                .projectDescription("projectDescription")
                .projectType(ProjectTypeDto.builder().id(1L).build())
                .captainId(2L)
                .currentTrackId(1L)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> underTest.create(teamCreationDto));
    }

    @Test
    void create() {
        TeamCreationDto teamCreationDto = TeamCreationDto.builder()
                .name("new")
                .projectDescription("projectDescription")
                .projectType(ProjectTypeDto.builder().id(1L).build())
                .captainId(5L)
                .currentTrackId(1L)
                .build();

        Team actual = underTest.create(teamCreationDto);

        Assertions.assertEquals(teamCreationDto.getName(), actual.getName());
        Assertions.assertEquals(teamCreationDto.getProjectDescription(), actual.getProjectDescription());
        Assertions.assertEquals(teamCreationDto.getCaptainId(), actual.getCaptainId());
        Assertions.assertEquals(1, actual.getQuantityOfStudents());
    }

    @Test
    void createOnExistingTeamShouldUpdate() {
        TeamCreationDto teamCreationDto = TeamCreationDto.builder()
                .name("Almost full")
                .projectDescription("new projectDescription")
                .projectType(ProjectTypeDto.builder().id(2L).build())
                .captainId(5L)
                .currentTrackId(1L)
                .build();

        Team actual = underTest.create(teamCreationDto);

        Assertions.assertEquals(teamCreationDto.getName(), actual.getName());
        Assertions.assertEquals(teamCreationDto.getProjectDescription(), actual.getProjectDescription());
        Assertions.assertEquals(teamCreationDto.getProjectType().getId(), actual.getProjectType().getId());
        Assertions.assertTrue(actual.getQuantityOfStudents() > 0);
    }

    @Test
    void delete() {
        Long teamId = 1L;
        Team team = teamRepository.findById(teamId).orElseThrow();
        List<Student> students =  team.getStudents();

        underTest.delete(teamId);
        for (Student student: students) {
            // current team has not been removed -> it must not be equal to the deleted team
            if (student.getCurrentTeam() != null) {
                Assertions.assertNotEquals(teamId, student.getCurrentTeam().getId());
            } else {
                // current team has been removed -> corresponding field must have been updated
                Assertions.assertEquals(false, student.getHasTeam());
                Assertions.assertEquals(false, student.getIsCaptain());
            }
        }
    }

    @Test
    void updateFromTeamCaptain() {
        Team beforeUpdateTeam = teamRepository.findById(2L).orElseThrow();

        TeamDto teamDto = TeamDto.builder()
                .name("about self") // should not be updated
                .projectDescription("contacts")
                .projectType(ProjectTypeDto.builder().id(1L).build())
                .quantityOfStudents(22) // should not be updated
                .build();

        Team actual = underTest.update(beforeUpdateTeam.getId(),
                teamDto,
                userService.findByIdOrElseThrow(
                        studentRepository.findById(beforeUpdateTeam.getCaptainId()).orElseThrow().getUser().getId()
                )
        );
        Assertions.assertEquals(teamDto.getProjectDescription(), actual.getProjectDescription());
        Assertions.assertEquals(teamDto.getProjectType().getId(), actual.getProjectType().getId());

        // shouldn't be updated
        Assertions.assertEquals(beforeUpdateTeam.getName(), actual.getName());
        Assertions.assertEquals(beforeUpdateTeam.getQuantityOfStudents(), actual.getQuantityOfStudents());
    }

    @Test
    void updateFromAdmin() {
        Team beforeUpdateTeam = teamRepository.findById(2L).orElseThrow();

        TeamDto teamDto = TeamDto.builder()
                .projectDescription("contacts")
                .projectType(ProjectTypeDto.builder().id(3L).build())
                .quantityOfStudents(beforeUpdateTeam.getQuantityOfStudents()-1) // should be updated
                .currentTrackId(3L) // the same as was
                .captain(StudentDto.builder().id(1L).build()) // should be updated
                .build();

        Team actual = underTest.update(beforeUpdateTeam.getId(),
                teamDto,
                userService.findByIdOrElseThrow(1L)
        );

        Assertions.assertEquals(teamDto.getProjectDescription(), actual.getProjectDescription());
        Assertions.assertEquals(teamDto.getProjectType().getId(), actual.getProjectType().getId());
        Assertions.assertEquals(beforeUpdateTeam.getQuantityOfStudents(), actual.getQuantityOfStudents());

        // clean up
        actual.setQuantityOfStudents(actual.getQuantityOfStudents() + 1);
        actual.setCaptainId(3L);
        teamRepository.save(actual);
    }

    @Test
    void updateFromForeignUser() {
        Team beforeUpdateTeam = teamRepository.findById(2L).orElseThrow();

        TeamDto teamDto = TeamDto.builder()
                .name("about self") // should not be updated
                .projectDescription("contacts")
                .projectType(ProjectTypeDto.builder().id(1L).build())
                .quantityOfStudents(22) // should not be updated
                .build();

        Assertions.assertThrows(ForbiddenException.class, () -> underTest.update(beforeUpdateTeam.getId(),
                teamDto,
                userService.findByIdOrElseThrow(2L)
        ));
    }

    @Test
    void searchByLike() {
        String like = "te";

        List<Team> actual = underTest.search(
                like,
                null,
                null,
                null,
                null
        );

        for (Team team : actual) {
            Assertions.assertTrue(team.getName().toLowerCase().contains(like));
        }

        Assertions.assertEquals(3, actual.size());
    }

    @Test
    void searchByTrack() {
        Long trackParam = 2L;

        List<Team> actual = underTest.search(
                null,
                trackParam,
                null,
                null,
                null
        );

        for (Team team : actual) {
            Assertions.assertEquals(trackParam, team.getCurrentTrack().getId());
        }

        Assertions.assertEquals(2, actual.size());
    }

    @Test
    void searchByIsFull() {
        Boolean isFullParam = true;

        List<Team> actual = underTest.search(
                null,
                null,
                isFullParam,
                null,
                null
        );

        for (Team team : actual) {
            Assertions.assertEquals(isFullParam, team.getIsFull());
        }

        Assertions.assertEquals(1, actual.size());
    }

    @Test
    void searchByProjectType() {
        String projectTypeParam = "Mobile";

        List<Team> actual = underTest.search(
                null,
                null,
                null,
                projectTypeParam,
                null
        );

        for (Team team : actual) {
            Assertions.assertEquals(projectTypeParam, team.getProjectType().getName());
        }

        Assertions.assertEquals(2, actual.size());
    }

    @Test
    void searchByTechnologies() {
        List<Long> technologiesParam = List.of(2L, 10L, 16L);

        List<Team> actual = underTest.search(
                null,
                null,
                null,
                null,
                technologiesParam
        );

        for (Team team : actual) {
            Assertions.assertTrue(team.getTechnologies()
                    .stream()
                    .map(Technology::getId)
                    .anyMatch(technologiesParam::contains)
            );
        }

        Assertions.assertEquals(4, actual.size());
    }

    @Test
    void getSecondYearsCount() {
        Team team = underTest.findByIdOrElseThrow(4L);

        int expected = 1;

        int actual = underTest.getSecondYearsCount(team);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void addStudentToTeamWhoHasTeamShouldFail() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> underTest.addStudentToTeam(
                        2L,
                        2L,
                        studentRepository.findById(2L).orElseThrow().getUser()
                )
        );
    }

    @Test
    void addStudentToFullTeamShouldFail() {
        Assertions.assertThrows(RuntimeException.class,
                () -> underTest.addStudentToTeam(
                        1004L,
                        11L,
                        studentRepository.findById(11L).orElseThrow().getUser()
                )
        );
    }

    @Test
    void addSecondYearStudentToTeamOverLimitShouldFail() {
        Assertions.assertThrows(RuntimeException.class,
                () -> underTest.addStudentToTeam(1003L,
                        13L,
                        studentRepository.findById(13L).orElseThrow().getUser()
                )
        );
    }

    @Test
    void addStudentToTeamInWhichWasMemberBeforeShouldFail() {
        Assertions.assertThrows(RuntimeException.class,
                () -> underTest.addStudentToTeam(
                        1003L,
                        1L,
                        studentRepository.findById(1L).orElseThrow().getUser()
                )
        );
    }

    @Test
    void addStudentToTeam() {
        Long teamId = 2L;
        Long studentId = 7L;

        underTest.addStudentToTeam(teamId, studentId, studentRepository.findById(studentId).orElseThrow().getUser());

        Student student = studentRepository.findById(studentId).orElseThrow();
        Team team = teamRepository.findById(teamId).orElseThrow();

        Assertions.assertTrue(student.getHasTeam());
        Assertions.assertEquals(teamId, student.getCurrentTeam().getId());
        Assertions.assertEquals(3, team.getQuantityOfStudents());
        Assertions.assertFalse(team.getIsFull());
        Assertions.assertTrue(team.getStudents().contains(student));
    }

    @Test
    void removeStudentFromTeam() {
        Student deleteStudent = studentRepository.findById(4L).orElseThrow();

        Team teamBeforeDelete = teamRepository.findById(deleteStudent.getCurrentTeam().getId()).orElseThrow();

        Team teamAfterDelete = underTest.removeStudentFromTeam(teamBeforeDelete, deleteStudent);

        Assertions.assertEquals(1, teamAfterDelete.getQuantityOfStudents());
        Assertions.assertEquals(false, teamAfterDelete.getIsFull());
        Assertions.assertEquals(1, teamAfterDelete.getStudents().size());
    }

    @Test
    void getSearchOptionsTeams() {
        TeamSearchOptionsDto actual = underTest.getSearchOptionsTeams(2L);

        Set<Long> expectedTechnologies = Set.of(4L, 21L, 22L, 24L, 10L, 28L, 29L, 47L, 48L);

        Assertions.assertEquals(
                Set.of(1L, 3L, 2L, 4L, 5L, 6L, 7L, 8L),
                actual.getProjectTypes().stream().map(ProjectTypeDto::getId).collect(Collectors.toUnmodifiableSet())
        );
        Assertions.assertEquals(
                expectedTechnologies,
                actual.getTechnologies().stream().map(TechnologyDto::getId).collect(Collectors.toUnmodifiableSet())
        );
    }

    @Test
    void getTeamHistoryForStudentWithManyTeams() {
        List<Team> actual = underTest.getTeamHistoryForStudent(2L);

        Assertions.assertEquals(2, actual.size());
    }

    @Test
    void getTeamHistoryForStudentWithoutAnyTeam() {
        List<Team> actual = underTest.getTeamHistoryForStudent(18L);

        Assertions.assertEquals(0, actual.size());
    }
}