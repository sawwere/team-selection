package ru.sfedu.teamselection.mapper.team;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.sfedu.teamselection.domain.ProjectType;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.team.ProjectTypeDto;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.mapper.ProjectTypeDtoMapper;
import ru.sfedu.teamselection.mapper.TechnologyDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamCreationDtoMapper;

class TeamCreationDtoMapperTest {
    @Mock
    private final TechnologyDtoMapper technologyDtoMapper = Mockito.mock(TechnologyDtoMapper.class);
    @Mock
    private final ProjectTypeDtoMapper projectTypeDtoMapper = Mockito.mock(ProjectTypeDtoMapper.class);


    @InjectMocks
    private TeamCreationDtoMapper underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.doReturn(
                TechnologyDto.builder()
                        .id(1L)
                        .name("tech")
                        .build()
        ).when (technologyDtoMapper).mapToDto(Mockito.notNull());
        Mockito.doReturn(
                Technology.builder()
                        .id(1L)
                        .name("tech")
                        .build()
        ).when (technologyDtoMapper).mapToEntity(Mockito.notNull());

        Mockito.doReturn(
                ProjectTypeDto.builder()
                        .id(2L)
                        .name("type")
                        .build()
        ).when (projectTypeDtoMapper).mapToDto(Mockito.notNull());
        Mockito.doReturn(
                ProjectType.builder()
                        .id(2L)
                        .name("type")
                        .build()
        ).when (projectTypeDtoMapper).mapToEntity(Mockito.notNull());
    }


    @Test
    void mapToEntity() {
        TeamCreationDto dto = TeamCreationDto.builder()
                .name("team name")
                .projectDescription("")
                .projectType(ProjectTypeDto.builder()
                        .id(2L)
                        .name("type")
                        .build())
                .captainId(1L)
                .technologies(List.of())
                .currentTrackId(1L)
                .build();

        Team expected = Team.builder()
                .id(null)
                .name(dto.getName())
                .projectDescription(dto.getProjectDescription())
                .projectType(ProjectType.builder()
                        .id(dto.getProjectType().getId())
                        .name(dto.getProjectType().getName())
                        .build())
                .captainId(dto.getCaptainId())
                .technologies(List.of())
                .currentTrack(Track.builder().id(dto.getCurrentTrackId()).build())
                .quantityOfStudents(0)
                .students(List.of())
                .isFull(false)
                .applications(List.of())
                .build();

        Team actual = underTest.mapToEntity(dto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getProjectDescription(), actual.getProjectDescription());
        Assertions.assertEquals(expected.getProjectType().getId(), actual.getProjectType().getId());
        Assertions.assertEquals(expected.getCaptainId(), actual.getCaptainId());
        Assertions.assertEquals(expected.getTechnologies().size(), actual.getTechnologies().size());
        Assertions.assertEquals(expected.getCurrentTrack().getId(), actual.getCurrentTrack().getId());
        Assertions.assertEquals(expected.getQuantityOfStudents(), actual.getQuantityOfStudents());
        Assertions.assertEquals(expected.getStudents().size(), actual.getStudents().size());
        Assertions.assertEquals(expected.getIsFull(), actual.getIsFull());
        Assertions.assertEquals(expected.getApplications().size(), actual.getApplications().size());
    }

    @Test
    void mapToDto() {
        Team entity = Team.builder()
                .id(null)
                .name("team name")
                .projectDescription("dto.getProjectDescription()")
                .projectType(ProjectType.builder()
                        .id(2L)
                        .name("type")
                        .build())
                .captainId(1L)
                .technologies(List.of())
                .currentTrack(Track.builder().id(12L).build())
                .quantityOfStudents(0)
                .students(List.of())
                .isFull(false)
                .applications(List.of())
                .build();

        TeamCreationDto expected = TeamCreationDto.builder()
                .name(entity.getName())
                .projectDescription(entity.getProjectDescription())
                .projectType(ProjectTypeDto.builder()
                        .id(entity.getProjectType().getId())
                        .name(entity.getProjectType().getName())
                        .build())
                .captainId(entity.getCaptainId())
                .technologies(List.of())
                .currentTrackId(entity.getCurrentTrack().getId())
                .build();

        TeamCreationDto actual = underTest.mapToDto(entity);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getProjectDescription(), actual.getProjectDescription());
        Assertions.assertEquals(expected.getProjectType().getId(), actual.getProjectType().getId());
        Assertions.assertEquals(expected.getCaptainId(), actual.getCaptainId());
        Assertions.assertEquals(expected.getTechnologies().size(), actual.getTechnologies().size());
        Assertions.assertEquals(expected.getCurrentTrackId(), actual.getCurrentTrackId());
    }
}