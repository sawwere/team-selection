package ru.sfedu.teamselection.mapper.application;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sfedu.teamselection.domain.ProjectType;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.application.ApplicationType;
import ru.sfedu.teamselection.domain.application.TeamInvite;
import ru.sfedu.teamselection.domain.application.TeamRequest;
import ru.sfedu.teamselection.dto.ProjectTypeDto;
import ru.sfedu.teamselection.dto.TechnologyDto;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.dto.application.ApplicationResponseDto;
import ru.sfedu.teamselection.dto.application.StudentApplicationDto;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ApplicationMapperTest {

    private final ApplicationMapper underTest = new ApplicationMapperImpl();

    @Test
    void mapCreationToEntity_shouldReturnNullWhenDtoIsNull() {
        assertThat(underTest.mapCreationToEntity(null)).isNull();
    }

    @Test
    void mapCreationToEntity_shouldMapAllFieldsCorrectly() {
        ApplicationCreationDto dto = ApplicationCreationDto.builder()
                .id(1L)
                .type(ApplicationType.INVITE)
                .status(ApplicationStatus.SENT)
                .teamId(2L)
                .studentId(3L)
                .build();

        Application result = underTest.mapCreationToEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(ApplicationType.INVITE);
        assertThat(result.getStatus()).isEqualTo("sent");
        assertThat(result.getTeam().getId()).isEqualTo(2L);
        assertThat(result.getStudent().getId()).isEqualTo(3L);
    }

    @Test
    void mapToCreationDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(underTest.mapToCreationDto(null)).isNull();
    }

    @Test
    void mapToCreationDto_shouldMapAllFieldsCorrectly() {
        Application entity = TeamRequest.builder()
                .id(1L)
                .type(ApplicationType.INVITE)
                .status("sent")
                .team(Team.builder().id(2L).build())
                .student(Student.builder().id(3L).build())
                .build();

        ApplicationCreationDto result = underTest.mapToCreationDto(entity);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(ApplicationType.REQUEST);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.SENT);
        assertThat(result.getTeamId()).isEqualTo(2L);
        assertThat(result.getStudentId()).isEqualTo(3L);
    }

    @Test
    void mapToResponseDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(underTest.mapToResponseDto(null)).isNull();
    }

    @Test
    void mapToResponseDto_shouldMapAllFieldsCorrectly() {
        Application entity = TeamRequest.builder()
                .id(1L)
                .type(ApplicationType.REQUEST)
                .status("rejected")
                .build();

        ApplicationResponseDto result = underTest.mapToResponseDto(entity);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(ApplicationType.REQUEST);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    void mapToDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(underTest.mapToDto(null)).isNull();
    }

    @Test
    void mapToDto_shouldMapAllFieldsCorrectly() {
        Application entity = TeamInvite.builder()
                .id(1L)
                .type(ApplicationType.INVITE)
                .status(ApplicationStatus.CANCELLED.toString())
                .team(Team.builder()
                        .id(2L)
                        .name("Team A")
                        .projectDescription("Description")
                        .projectType(ProjectType.builder().id(3L).name("Web").build())
                        .captainId(4L)
                        .technologies(List.of(Technology.builder().id(5L).name("Java").build()))
                        .build())
                .student(Student.builder()
                        .id(6L)
                        .course(2)
                        .groupNumber(1)
                        .aboutSelf("About")
                        .contacts("Contacts")
                        .user(User.builder().id(7L).fio("John Doe").build())
                        .build())
                .build();

        ApplicationDto result = underTest.mapToDto(entity);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(ApplicationType.INVITE);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.CANCELLED);

        assertThat(result.getTeam().getId()).isEqualTo(2L);
        assertThat(result.getTeam().getName()).isEqualTo("Team A");
        assertThat(result.getTeam().getProjectDescription()).isEqualTo("Description");
        assertThat(result.getTeam().getProjectType().getId()).isEqualTo(3L);
        assertThat(result.getTeam().getCaptainId()).isEqualTo(4L);
        assertThat(result.getTeam().getTechnologies()).hasSize(1);

        assertThat(result.getStudent().getId()).isEqualTo(6L);
        assertThat(result.getStudent().getFio()).isEqualTo("John Doe");
        assertThat(result.getStudent().getUserId()).isEqualTo(7L);
        assertThat(result.getStudent().getCourse()).isEqualTo(2);
        assertThat(result.getStudent().getGroupNumber()).isEqualTo(1);
        assertThat(result.getStudent().getAboutSelf()).isEqualTo("About");
        assertThat(result.getStudent().getContacts()).isEqualTo("Contacts");
    }

    @Test
    void mapToEntity_shouldReturnNullWhenDtoIsNull() {
        assertThat(underTest.mapToEntity(null)).isNull();
    }

    @Test
    void mapToEntity_shouldMapAllFieldsCorrectly() {
        ApplicationDto dto = ApplicationDto.builder()
                .id(1L)
                .type(ApplicationType.INVITE)
                .status(ApplicationStatus.CANCELLED)
                .team(TeamCreationDto.builder()
                        .id(2L)
                        .name("Team A")
                        .projectDescription("Description")
                        .projectType(new ProjectTypeDto().id(3L).name("Web"))
                        .captainId(4L)
                        .technologies(List.of(new TechnologyDto().id(5L).name("Java")))
                        .build())
                .student(StudentApplicationDto.builder()
                        .id(6L)
                        .userId(7L)
                        .fio("John Doe")
                        .course(2)
                        .groupNumber(1)
                        .aboutSelf("About")
                        .contacts("Contacts")
                        .build())
                .build();

        Application result = underTest.mapToEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(ApplicationType.INVITE);
        assertThat(result.getStatus()).isEqualTo("cancelled");

        assertThat(result.getTeam().getId()).isEqualTo(2L);
        assertThat(result.getTeam().getName()).isEqualTo("Team A");
        assertThat(result.getTeam().getProjectDescription()).isEqualTo("Description");
        assertThat(result.getTeam().getProjectType().getId()).isEqualTo(3L);
        assertThat(result.getTeam().getCaptainId()).isEqualTo(4L);
        assertThat(result.getTeam().getTechnologies()).hasSize(1);

        assertThat(result.getStudent().getId()).isEqualTo(6L);
        assertThat(result.getStudent().getUser().getId()).isEqualTo(7L);
        assertThat(result.getStudent().getUser().getFio()).isEqualTo("John Doe");
        assertThat(result.getStudent().getCourse()).isEqualTo(2);
        assertThat(result.getStudent().getGroupNumber()).isEqualTo(1);
        assertThat(result.getStudent().getAboutSelf()).isEqualTo("About");
        assertThat(result.getStudent().getContacts()).isEqualTo("Contacts");
    }

    @Test
    void mapToDto_shouldHandleEmptyTechnologyList() {
        Application entity = TeamRequest.builder()
                .team(Team.builder().technologies(List.of()).build())
                .student(Student.builder().build())
                .build();

        ApplicationDto result = underTest.mapToDto(entity);

        assertThat(result.getTeam().getTechnologies()).isEmpty();
    }
}
