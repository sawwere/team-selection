package ru.sfedu.teamselection.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.BasicTestContainerTest;
import ru.sfedu.teamselection.TeamSelectionApplication;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.StudentCreationDto;
import ru.sfedu.teamselection.dto.StudentDto;
import ru.sfedu.teamselection.dto.StudentSearchOptionsDto;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.mapper.TechnologyDtoMapper;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.TeamRepository;
import ru.sfedu.teamselection.repository.TechnologyRepository;

@SpringBootTest(classes = TeamSelectionApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource("/application-test.yml")
class StudentServiceTest extends BasicTestContainerTest {
    @Autowired
    @InjectMocks
    private StudentService underTest;
    @Autowired
    private UserService userService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    @SpyBean
    private StudentRepository studentRepository;

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private TechnologyDtoMapper technologyDtoMapper;


    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        Mockito.doNothing().when(studentRepository).delete((Student) Mockito.notNull());

        Mockito.doAnswer(x ->
        {
            Student student = x.getArgument(0);
            student.setId(100L);
            return student;
        }).when (studentRepository).save(Mockito.notNull());
    }

    @Test
    void findByIdOrElseThrow() {
        Student expected = studentRepository.findById(1L).orElseThrow();

        Student actual = underTest.findByIdOrElseThrow(1L);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getCourse(), actual.getCourse());
        Assertions.assertEquals(expected.getAboutSelf(), actual.getAboutSelf());
        Assertions.assertEquals(expected.getHasTeam(), actual.getHasTeam());
    }

    @Test
    void findAll() {
        List<Student> expected = studentRepository.findAll();

        List<Student> actual = underTest.findAll();

        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    void create() {
        StudentCreationDto studentDto = StudentCreationDto.builder()
                .aboutSelf("about self")
                .contacts("contacts")
                .course(2)
                .groupNumber(2)
                .userId(21L)
                .build();

        Student actual = underTest.create(studentDto);

        Assertions.assertEquals(studentDto.getAboutSelf(), actual.getAboutSelf());
        Assertions.assertEquals(studentDto.getContacts(), actual.getContacts());
        Assertions.assertEquals(studentDto.getCourse(), actual.getCourse());
        Assertions.assertEquals(studentDto.getGroupNumber(), actual.getGroupNumber());
    }

    @Test
    void deleteStudentWithoutTeam() {
        underTest.delete(1L);
    }

    @Test
    @Transactional
    void deleteStudentWithTeam() {
        Student deleteStudent = studentRepository.findById(4L).orElseThrow();

        Team teamBeforeDelete = teamRepository.findById(deleteStudent.getCurrentTeam().getId()).orElseThrow();

        underTest.delete(4L);
        Team teamAfterDelete = teamRepository.findById(teamBeforeDelete.getId()).orElseThrow();

        Assertions.assertEquals(1, teamAfterDelete.getQuantityOfStudents());
        Assertions.assertEquals(false, teamAfterDelete.getIsFull());
        Assertions.assertEquals(1, teamAfterDelete.getStudents().size());
    }

    @Test
    @Transactional
    void deleteCaptainFromTeam() {
        Student deleteStudent = studentRepository.findById(3L).orElseThrow();

        Team teamBeforeDelete = teamRepository.findById(deleteStudent.getCurrentTeam().getId()).orElseThrow();
        Assertions.assertThrows(ConstraintViolationException.class, () -> underTest.delete(3L));
    }

    @Test
    void searchByLike() {
        String like = "Серг";

        List<Student> actual = underTest.search(
                like,
                null,
                null,
                null,
                null,
                null
        );

        for (Student student : actual) {
            Assertions.assertTrue(student.getUser().getFio().contains(like));
        }

        Assertions.assertEquals(5, actual.size());
    }

    @Test
    void searchByCourse() {
        Integer courseParam = 1;

        List<Student> actual = underTest.search(
                null,
                courseParam,
                null,
                null,
                null,
                null
        );

        for (Student student : actual) {
            Assertions.assertEquals(courseParam, student.getCourse());
        }

        Assertions.assertEquals(8, actual.size());
    }

    @Test
    void searchByGroup() {
        Integer groupParam = 1;

        List<Student> actual = underTest.search(
                null,
                null,
                groupParam,
                null,
                null,
                null
        );

        for (Student student : actual) {
            Assertions.assertEquals(groupParam, student.getGroupNumber());
        }

        Assertions.assertEquals(19, actual.size());
    }

    @Test
    void searchByHasTeam() {
        Boolean hasTeamParam = true;

        List<Student> actual = underTest.search(
                null,
                null,
                null,
                hasTeamParam,
                null,
                null
        );

        for (Student student : actual) {
            Assertions.assertEquals(hasTeamParam, student.getHasTeam());
        }

        Assertions.assertEquals(11, actual.size());
    }

    @Test
    void searchByIsCaptain() {
        Boolean isCaptainParam = true;

        List<Student> actual = underTest.search(
                null,
                null,
                null,
                null,
                isCaptainParam,
                null
        );

        for (Student student : actual) {
            Assertions.assertEquals(isCaptainParam, student.getIsCaptain());
        }

        Assertions.assertEquals(4, actual.size());
    }

    @Test
    @Transactional
    void searchByTechnologies() {
        List<Long> technologiesParam = List.of(1L, 2L, 5L);

        List<Student> actual = underTest.search(
                null,
                null,
                null,
                null,
                null,
                List.of(1L, 2L, 5L)
        );

        for (Student student : actual) {
            Assertions.assertTrue(student.getTechnologies()
                    .stream()
                    .map(Technology::getId)
                    .anyMatch(technologiesParam::contains)
            );
        }

        Assertions.assertEquals(7, actual.size());
    }

    @Test
    @Transactional
    void updateFromStudentThemself() {
        Student beforeUpdateStudent = studentRepository.findById(2L).orElseThrow();

        StudentDto studentDto = StudentDto.builder()
                .aboutSelf("about self")
                .contacts("contacts")
                .course(2)
                .groupNumber(2)
                .hasTeam(!beforeUpdateStudent.getHasTeam())
                .isCaptain(!beforeUpdateStudent.getHasTeam())
                .build();

        Student actual = underTest.update(beforeUpdateStudent.getId(),
                studentDto,
                userService.findByIdOrElseThrow(beforeUpdateStudent.getUser().getId())
        );

        Assertions.assertEquals(studentDto.getAboutSelf(), actual.getAboutSelf());
        Assertions.assertEquals(studentDto.getContacts(), actual.getContacts());
        Assertions.assertEquals(studentDto.getCourse(), actual.getCourse());
        Assertions.assertEquals(studentDto.getGroupNumber(), actual.getGroupNumber());

        // shouldn't be updated
        Assertions.assertEquals(beforeUpdateStudent.getHasTeam(), actual.getHasTeam());
        Assertions.assertEquals(beforeUpdateStudent.getIsCaptain(), actual.getIsCaptain());
    }

    @Test
    @Transactional
    void updateFromAdmin() {
        Student beforeUpdateStudent = studentRepository.findById(2L).orElseThrow();

        StudentDto studentDto = StudentDto.builder()
                .aboutSelf("about self")
                .contacts("contacts")
                .course(2)
                .groupNumber(2)
                .hasTeam(!beforeUpdateStudent.getHasTeam())
                .isCaptain(!beforeUpdateStudent.getHasTeam())
                .build();

        Student actual = underTest.update(beforeUpdateStudent.getId(),
                studentDto,
                userService.findByIdOrElseThrow(1L)
        );

        Assertions.assertEquals(studentDto.getAboutSelf(), actual.getAboutSelf());
        Assertions.assertEquals(studentDto.getContacts(), actual.getContacts());
        Assertions.assertEquals(studentDto.getCourse(), actual.getCourse());
        Assertions.assertEquals(studentDto.getGroupNumber(), actual.getGroupNumber());

        // should be updated
        Assertions.assertEquals(studentDto.getHasTeam(), actual.getHasTeam());
        Assertions.assertEquals(studentDto.getIsCaptain(), actual.getIsCaptain());
    }

    @Test
    @Transactional
    void updateFromForeignUser() {
        Student beforeUpdateStudent = studentRepository.findById(2L).orElseThrow();

        StudentDto studentDto = StudentDto.builder()
                .aboutSelf("about self")
                .contacts("contacts")
                .course(2)
                .groupNumber(2)
                .hasTeam(!beforeUpdateStudent.getHasTeam())
                .isCaptain(!beforeUpdateStudent.getHasTeam())
                .build();

        Assertions.assertThrows(AccessDeniedException.class, () -> underTest.update(beforeUpdateStudent.getId(),
                studentDto,
                userService.findByIdOrElseThrow(10L)
        ));
    }

    @Test
    void typeOfStudentTrack() {

        for (int i = 0; i < 10; i++) {
            TrackType expected;
            if (i == 1 || i == 2) {
                expected = TrackType.bachelor;
            } else if (i == 5) {
                expected = TrackType.master;
            } else {
                expected = null;
            }
            Assertions.assertEquals(expected, underTest.typeOfStudentTrack(Student.builder().course(i).build()));
        }
    }

    @Test
    void getSearchOptionsStudents() {
        StudentSearchOptionsDto actual = underTest.getSearchOptionsStudents();

        Set<TechnologyDto> expectedTechnologies = new HashSet<>(
                technologyDtoMapper.mapListToDto(technologyRepository.findAll())
        );

        Assertions.assertEquals(Set.of(1, 2, 5), actual.getCourses());
        Assertions.assertEquals(Set.of(1), actual.getGroups());
        Assertions.assertEquals(List.of(true, false), actual.getHasTeam());
        Assertions.assertEquals(List.of(true, false), actual.getIsCaptain());
        Assertions.assertEquals(expectedTechnologies, actual.getTechnologies());

    }

    @Test
    void getCurrentStudent() {
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Student expected = studentRepository.findById(1L).orElseThrow();

        Mockito.doReturn(new Authentication() {

            @Override
            public String getName() {
                return expected.getUser().getName();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return expected.getUser().getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return expected.getUser();
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        }).when(securityContext).getAuthentication();

        var actual = underTest.getCurrentStudent();
        Assertions.assertEquals(1L, actual);
    }

    @Test
    void getCurrentStudentReturnNullForNonStudentUser() {
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = userService.findByUsername("admin");

        Mockito.doReturn(new Authentication() {

            @Override
            public String getName() {
                return user.getName();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return user.getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return user;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        }).when(securityContext).getAuthentication();

        var actual = underTest.getCurrentStudent();
        Assertions.assertNull(actual);
    }
}