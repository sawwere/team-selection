package ru.sfedu.teamselection.mapper.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamCreationDtoMapper;

@Component
@RequiredArgsConstructor
public class ApplicationDtoMapper implements DtoMapper<ApplicationDto, Application> {


    private final TeamCreationDtoMapper teamCreationDtoMapper;

    private final StudentApplicationDtoMapper studentApplicationDtoMapper;


    @Override
    public Application mapToEntity(ApplicationDto dto) {
        return Application.builder()
                .id(dto.getId())
                .team(teamCreationDtoMapper.mapToEntity(dto.getTeam()))
                .status(dto.getStatus())
                .student(studentApplicationDtoMapper.mapToEntity(dto.getStudent()))
                .build();
    }

    @Override
    public ApplicationDto mapToDto(Application entity) {

        return ApplicationDto.builder()
                .id(entity.getId())
                .team(teamCreationDtoMapper.mapToDto(entity.getTeam()))
                .status(entity.getStatus())
                .student(studentApplicationDtoMapper.mapToDto(entity.getStudent()))
                .build();
    }
}
