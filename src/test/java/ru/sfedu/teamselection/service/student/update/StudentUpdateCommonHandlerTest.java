package ru.sfedu.teamselection.service.student.update;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.StudentUpdateDto;
import ru.sfedu.teamselection.dto.StudentUpdateTeamDto;
import ru.sfedu.teamselection.dto.StudentUpdateUserDto;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.service.TeamService;

public class StudentUpdateCommonHandlerTest {
    private final TeamService teamService = Mockito.mock(TeamService.class);
    private final TechnologyMapper technologyMapper = Mockito.mock(TechnologyMapper.class);

    private final StudentUpdateCommonHandler underTest = new StudentUpdateCommonHandler(
            teamService,
            technologyMapper
    );

    @BeforeEach
    void setUp() {
        Mockito.doReturn(List.of(
                Technology.builder()
                        .id(1L)
                        .name("technology name")
                        .build()
                )
        ).when(technologyMapper).mapListToEntity(
                Mockito.any()
        );
    }

    @Test
    void whenAllFieldsInDtoThenUpdateAllFields() {
        // given
        var student = createStudent();

        StudentUpdateDto studentDto = new StudentUpdateDto()
                .aboutSelf("new about self")
                .contacts("new contacts")
                .course(2)
                .groupNumber(22)
                .technologies(List.of(
                        new TechnologyDto()
                                .id(1L)
                                .name("name")
                ))
                .currentTeam(new StudentUpdateTeamDto().id(student.getCurrentTeam().getId()))
                .user(new StudentUpdateUserDto().id(1L).fio("newFio"));

        // when
        underTest.update(student, studentDto);

        // then
        Assertions.assertEquals(studentDto.getAboutSelf(), student.getAboutSelf());
        Assertions.assertEquals(studentDto.getContacts(), student.getContacts());
        Assertions.assertEquals(1, student.getTechnologies().size());
    }

    @Test
    void whenNullsInDtoThenDoNotUpdateThoseFields() {
        // given
        var student = createStudent();

        StudentUpdateDto studentDto = new StudentUpdateDto()
                .aboutSelf(null)
                .contacts(null)
                .course(2)
                .groupNumber(22)
                .technologies(null)
                .currentTeam(new StudentUpdateTeamDto().id(student.getCurrentTeam().getId()))
                .user(new StudentUpdateUserDto().id(1L).fio("newFio"));

        // when
        underTest.update(student, studentDto);

        // then
        Assertions.assertNotNull(student.getAboutSelf());
        Assertions.assertNotNull(student.getContacts());
        Assertions.assertNotNull(student.getTechnologies());
    }

    private Student createStudent() {
        return Student.builder()
                .id(2L)
                .course(1)
                .groupNumber(1)
                .isCaptain(false)
                .hasTeam(true)
                .aboutSelf("about self")
                .contacts("contacts")
                .technologies(List.of(
                        Technology.builder()
                                .id(1L)
                                .name("name")
                                .build()
                ))
                .currentTeam(Team.builder()
                        .id(1L)
                        .build()
                )
                .user(User.builder()
                        .id(3L)
                        .fio("fio")
                        .build()
                )
                .currentTrack(Track.builder()
                        .id(1L)
                        .build()
                )
                .build();
    }
}
