package ru.sfedu.teamselection.mapper.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.ApplicationCreationDto;
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
        return Application.builder()
                .id(dto.getId())
                .team(Team.builder().id(dto.getTeamId()).build())
                .status(dto.getStatus())
                .student(Student.builder().id(dto.getStudentId()).build())
                .build();
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
                .status(entity.getStatus())
                .studentId(entity.getStudent().getId())
                .build();
    }
}
