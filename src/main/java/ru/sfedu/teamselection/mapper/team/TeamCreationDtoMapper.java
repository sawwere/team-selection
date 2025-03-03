package ru.sfedu.teamselection.mapper.team;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.ProjectTypeMapper;
import ru.sfedu.teamselection.mapper.TechnologyMapper;

@Component
@RequiredArgsConstructor
public class TeamCreationDtoMapper implements DtoMapper<TeamCreationDto, Team> {
    private final TechnologyMapper technologyDtoMapper;
    private final ProjectTypeMapper projectTypeDtoMapper;

    @Override
    public Team mapToEntity(TeamCreationDto dto) {
        return Team.builder()
                .name(dto.getName())
                .projectDescription(dto.getProjectDescription())
                .projectType(projectTypeDtoMapper.mapToEntity(dto.getProjectType()))
                .captainId(dto.getCaptainId())
                .students(new ArrayList<>())
                .applications(new ArrayList<>())
                .technologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()))
                .currentTrack(Track.builder().id(dto.getCurrentTrackId()).build())
                .build();
    }

    @Override
    public TeamCreationDto mapToDto(Team entity) {
        return TeamCreationDto.builder()
                .name(entity.getName())
                .projectDescription(entity.getProjectDescription())
                .projectType(projectTypeDtoMapper.mapToDto(entity.getProjectType()))
                .captainId(entity.getCaptainId())
                .technologies(technologyDtoMapper.mapListToDto(entity.getTechnologies()))
                .currentTrackId(entity.getCurrentTrack().getId())
                .build();
    }
}
