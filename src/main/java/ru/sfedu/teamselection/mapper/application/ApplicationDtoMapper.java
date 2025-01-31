package ru.sfedu.teamselection.mapper.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.application.TeamInvite;
import ru.sfedu.teamselection.domain.application.TeamRequest;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamCreationDtoMapper;

@Component
@RequiredArgsConstructor
public class ApplicationDtoMapper implements DtoMapper<ApplicationDto, Application> {


    private final TeamCreationDtoMapper teamCreationDtoMapper;

    private final StudentApplicationDtoMapper studentApplicationDtoMapper;


    @Override
    public Application mapToEntity(ApplicationDto dto) {
        return switch (dto.getType()) {
            case INVITE -> TeamInvite.builder()
                    .id(dto.getId())
                    .team(teamCreationDtoMapper.mapToEntity(dto.getTeam()))
                    .status(dto.getStatus().toString())
                    .student(studentApplicationDtoMapper.mapToEntity(dto.getStudent()))
                    .build();
            case REQUEST -> TeamRequest.builder()
                    .id(dto.getId())
                    .team(teamCreationDtoMapper.mapToEntity(dto.getTeam()))
                    .status(dto.getStatus().toString())
                    .student(studentApplicationDtoMapper.mapToEntity(dto.getStudent()))
                    .build();
        };
    }

    @Override
    public ApplicationDto mapToDto(Application entity) {
        return ApplicationDto.builder()
                .id(entity.getId())
                .team(teamCreationDtoMapper.mapToDto(entity.getTeam()))
                .status(ApplicationStatus.of(entity.getStatus()))
                .student(studentApplicationDtoMapper.mapToDto(entity.getStudent()))
                .type(entity.getType())
                .build();
    }
}
