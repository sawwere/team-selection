package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.ApplicationDto;

@Component
@RequiredArgsConstructor
public class ApplicationDtoMapper implements DtoMapper<ApplicationDto, Application> {
    /**
     * Map Dto to Entity
     *
     * @param dto Dto object to be mapped
     * @return mapped entity
     */
    @Override
    public Application mapToEntity(ApplicationDto dto) {
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
    public ApplicationDto mapToDto(Application entity) {
        return ApplicationDto.builder()
                .id(entity.getId())
                .teamId(entity.getTeam().getId())
                .status(entity.getStatus())
                .studentId(entity.getStudent().getId())
                .build();
    }
}
