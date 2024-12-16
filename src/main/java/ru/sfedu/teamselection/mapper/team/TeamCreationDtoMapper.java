package ru.sfedu.teamselection.mapper.team;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.TeamCreationDto;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.TechnologyDtoMapper;
import ru.sfedu.teamselection.repository.TrackRepository;

@Component
@RequiredArgsConstructor
public class TeamCreationDtoMapper implements DtoMapper<TeamCreationDto, Team> {
    private final TechnologyDtoMapper technologyDtoMapper;
    private final TrackRepository trackRepository;

    @Override
    public Team mapToEntity(TeamCreationDto dto) {
        return Team.builder()
                .id(dto.getId())
                .name(dto.getName())
                .projectDescription(dto.getProjectDescription())
                .projectType(dto.getProjectType())
                .captainId(dto.getCaptainId())
                .students(new ArrayList<>())
                .applications(new ArrayList<>())
                .technologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()))
                .currentTrack(trackRepository.findById(dto.getCurrentTrackId()).orElseThrow())
                .build();
    }

    @Override
    public TeamCreationDto mapToDto(Team entity) {
        return TeamCreationDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .projectDescription(entity.getProjectDescription())
                .projectType(entity.getProjectType())
                .captainId(entity.getCaptainId())
                .technologies(technologyDtoMapper.mapListToDto(entity.getTechnologies()))
                .currentTrackId(entity.getCurrentTrack().getId())
                .build();
    }
}
