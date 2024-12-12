package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.TeamCreationDto;

@Component
@RequiredArgsConstructor
public class TeamCreationDtoMapper implements DtoMapper<TeamCreationDto, Team>{
    @Override
    public Team mapToEntity(TeamCreationDto dto) {
        return Team.builder()
                .name(dto.getName())
                .projectDescription(dto.getAbout())
                .projectType(dto.getProjectType())
                .captainId(dto.getCaptainId())
                .technologies(dto.getTags().toString())
                .tags(dto.getTags())
                .currentTrackId(dto.getCurrentTrackId())
                .build();
    }

    @Override
    public TeamCreationDto mapToDto(Team entity) {
        return TeamCreationDto.builder()
                .name(entity.getName())
                .about(entity.getAbout())
                .projectType(entity.getProjectType())
                .captainId(entity.getCaptainId())
                .tags(entity.getTags())
                .currentTrackId(entity.getCurrentTrackId())
                .build();
    }
}
