package ru.sfedu.teamselection.mapper.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.application.ApplicationType;
import ru.sfedu.teamselection.domain.application.TeamInvite;
import ru.sfedu.teamselection.domain.application.TeamRequest;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;

class ApplicationCreationDtoMapperTest {

    private final ApplicationMapper underTest = ApplicationMapper.INSTANCE;


    @Test
    void mapRequestToEntity() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1L)
                .studentId(10L)
                .teamId(24L)
                .status(ApplicationStatus.CANCELLED)
                .type(ApplicationType.REQUEST)
                .build();

        Application expected = TeamRequest.builder()
                .id(1L)
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .status(dto.getStatus().toString())
                .build();

        Application actual = underTest.mapToEntity(dto);
        Assertions.assertInstanceOf(TeamRequest.class, actual);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void mapInviteToEntity() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1L)
                .studentId(10L)
                .teamId(24L)
                .status(ApplicationStatus.CANCELLED)
                .type(ApplicationType.INVITE)
                .build();

        Application expected = TeamInvite.builder()
                .id(1L)
                .student(Student.builder().id(dto.getStudentId()).build())
                .team(Team.builder().id(dto.getTeamId()).build())
                .status(dto.getStatus().toString())
                .build();

        Application actual = underTest.mapToEntity(dto);
        Assertions.assertInstanceOf(TeamInvite.class, actual);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getTeam().getId(), actual.getTeam().getId());
        Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());

    }

    @Test
    void mapRequestToDto() {
        Application entity = TeamRequest.builder()
                .id(1L)
                .student(Student.builder().id(12L).build())
                .team(Team.builder().id(32L).build())
                .status("Sent")
                .build();

        ApplicationCreationDto expected = ApplicationCreationDto.builder()
                .id(1L)
                .studentId(entity.getStudent().getId())
                .teamId(entity.getTeam().getId())
                .status(ApplicationStatus.SENT)
                .type(ApplicationType.REQUEST)
                .build();

        ApplicationCreationDto actual = underTest.mapToDto(entity);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getTeamId(), actual.getTeamId());
        Assertions.assertEquals(expected.getStudentId(), actual.getStudentId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    void mapInviteToDto() {
        Application entity = TeamInvite.builder()
                .id(1L)
                .student(Student.builder().id(12L).build())
                .team(Team.builder().id(32L).build())
                .status("Sent")
                .build();

        ApplicationCreationDto expected = ApplicationCreationDto.builder()
                .id(1L)
                .studentId(entity.getStudent().getId())
                .teamId(entity.getTeam().getId())
                .status(ApplicationStatus.SENT)
                .type(ApplicationType.INVITE)
                .build();

        ApplicationCreationDto actual = underTest.mapToDto(entity);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getTeamId(), actual.getTeamId());
        Assertions.assertEquals(expected.getStudentId(), actual.getStudentId());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }
}