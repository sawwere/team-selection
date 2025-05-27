package ru.sfedu.teamselection.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.BasicTestContainerTest;
import ru.sfedu.teamselection.TeamSelectionApplication;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.student.StudentCreationDto;
import ru.sfedu.teamselection.dto.student.StudentDto;
import ru.sfedu.teamselection.dto.student.StudentSearchOptionsDto;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.TeamRepository;
import ru.sfedu.teamselection.repository.TechnologyRepository;

@SpringBootTest(classes = TeamSelectionApplication.class)
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
    @MockitoSpyBean
    private StudentRepository studentRepository;

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private TechnologyMapper technologyDtoMapper;

    private final int defaultPage = 0;
    private final int defaultPageSize = 10;
    private final Sort.Direction defaultDirection = Sort.Direction.ASC;
    private final String defaultSort = "name";
    private final Pageable pageable = PageRequest.of(
            defaultPage,
            defaultPageSize,
            Sort.by(defaultDirection, defaultSort)
    );


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

    @Transactional
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
    @Transactional
    void findAll() {
        List<Student> expected = studentRepository.findAll();

        List<Student> actual = underTest.findAll();

        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    @Transactional
    void create() {
        StudentCreationDto studentDto = StudentCreationDto.builder()
                .aboutSelf("about self")
                .contacts("contacts")
                .course(2)
                .groupNumber(2)
                .userId(21L)
                .trackId(1L)
                .build();

        Student actual = underTest.create(studentDto);

        Assertions.assertEquals(studentDto.getAboutSelf(), actual.getAboutSelf());
        Assertions.assertEquals(studentDto.getContacts(), actual.getContacts());
        Assertions.assertEquals(studentDto.getCourse(), actual.getCourse());
        Assertions.assertEquals(studentDto.getGroupNumber(), actual.getGroupNumber());
    }

    @Test
    @Transactional
    void deleteStudentWithoutTeam() {
        underTest.delete(1L);
    }

    @Test
    @Transactional
    void deleteStudentWithTeam() {
        Student deleteStudent = studentRepository.findById(8L).orElseThrow();

        Team teamBeforeDelete = teamRepository.findById(deleteStudent.getCurrentTeam().getId()).orElseThrow();

        underTest.delete(8L);
        Team teamAfterDelete = teamRepository.findById(teamBeforeDelete.getId()).orElseThrow();

        Assertions.assertEquals(4, teamAfterDelete.getQuantityOfStudents());
        Assertions.assertEquals(false, teamAfterDelete.getIsFull());
        Assertions.assertEquals(4, teamAfterDelete.getStudents().size());
    }

    @Test
    @Transactional
    void deleteCaptainFromTeam() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> underTest.delete(3L));
    }

    @Test
    @Transactional
    void searchByLike() {
        String like = "Серг";

        Page<Student> actual = underTest.search(
                like,
                null,
                null,
                null,
                null,
                null,
                List.of(),
                pageable
        );

        for (Student student : actual) {
            Assertions.assertTrue(student.getUser().getFio().contains(like));
        }

        Assertions.assertEquals(5, actual.getTotalElements());
    }

    @Test
    @Transactional
    void searchByCourse() {
        Integer courseParam = 1;

        Page<Student> actual = underTest.search(
                null,
                null,
                courseParam,
                null,
                null,
                null,
                List.of(),
                pageable
        );

        for (Student student : actual) {
            Assertions.assertEquals(courseParam, student.getCourse());
        }

        Assertions.assertEquals(8, actual.getTotalElements());
    }

    @Test
    @Transactional
    void searchByGroup() {
        Integer groupParam = 1;

        Page<Student> actual = underTest.search(
                null,
                null,
                null,
                groupParam,
                null,
                null,
                null,
                pageable
        );

        for (Student student : actual) {
            Assertions.assertEquals(groupParam, student.getGroupNumber());
        }

        Assertions.assertEquals(19, actual.getTotalElements());
    }

    @Test
    @Transactional
    void searchByHasTeam() {
        Boolean hasTeamParam = true;

        Page<Student> actual = underTest.search(
                null,
                null,
                null,
                null,
                hasTeamParam,
                null,
                null,
                pageable
        );

        for (Student student : actual) {
            Assertions.assertEquals(hasTeamParam, student.getHasTeam());
        }

        Assertions.assertEquals(10, actual.getTotalElements());
    }

    @Test
    @Transactional
    void searchByIsCaptain() {
        Boolean isCaptainParam = true;

        Page<Student> actual = underTest.search(
                null,
                null,
                null,
                null,
                null,
                isCaptainParam,
                null,
                pageable
        );

        for (Student student : actual) {
            Assertions.assertEquals(isCaptainParam, student.getIsCaptain());
        }

        Assertions.assertEquals(4, actual.getTotalElements());
    }

    @Test
    @Transactional
    void searchByTechnologies() {
        List<Long> technologiesParam = List.of(1L, 2L, 5L);

        Page<Student> actual = underTest.search(
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(1L, 2L, 5L),
                pageable
        );

        for (Student student : actual) {
            Assertions.assertTrue(student.getTechnologies()
                    .stream()
                    .map(Technology::getId)
                    .anyMatch(technologiesParam::contains)
            );
        }

        Assertions.assertEquals(7, actual.getTotalElements());
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
                .currentTeam(TeamDto.builder().id(beforeUpdateStudent.getCurrentTeam().getId()).build())
                .build();

        Student actual = underTest.update(
                beforeUpdateStudent.getId(),
                studentDto,
                false
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
                .currentTeam(TeamDto.builder().id(beforeUpdateStudent.getCurrentTeam().getId()).build())
                .build();

        Student actual = underTest.update(
                beforeUpdateStudent.getId(),
                studentDto,
                true
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
    @Transactional
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
    @Transactional
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
    @Transactional
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