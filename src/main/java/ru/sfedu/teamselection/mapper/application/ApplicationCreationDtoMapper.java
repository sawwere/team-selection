package ru.sfedu.teamselection.mapper.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.application.TeamInvite;
import ru.sfedu.teamselection.domain.application.TeamRequest;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.mapper.DtoMapper;

@Component
@RequiredArgsConstructor
public class ApplicationCreationDtoMapper implements DtoMapper<ApplicationCreationDto, Application> {
    /**
     * Map Dto to Entity
     *
     * @param dto Dto object to be mapped
     * @return mapped entity
     */
    @Override
    public Application mapToEntity(ApplicationCreationDto dto) {
        return switch (dto.getType()) {
            case INVITE -> TeamInvite.builder()
                    .id(dto.getId())
                    .team(Team.builder().id(dto.getTeamId()).build())
                    .status(dto.getStatus().toString())
                    .student(Student.builder().id(dto.getStudentId()).build())
                    .build();
            case REQUEST -> TeamRequest.builder()
                    .id(dto.getId())
                    .team(Team.builder().id(dto.getTeamId()).build())
                    .status(dto.getStatus().toString())
                    .student(Student.builder().id(dto.getStudentId()).build())
                    .build();
        };
    }

    /**
     * Map Entity to Dto
     *
     * @param entity Entity object to be mapped
     * @return mapped dto
     */
    @Override
    public ApplicationCreationDto mapToDto(Application entity) {
        return ApplicationCreationDto.builder()
                .id(entity.getId())
                .teamId(entity.getTeam().getId())
                .status(ApplicationStatus.of(entity.getStatus()))
                .studentId(entity.getStudent().getId())
                .type(entity.getType())
                .build();
    }
}
