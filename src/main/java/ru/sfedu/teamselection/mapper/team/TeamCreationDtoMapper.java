package ru.sfedu.teamselection.mapper.team;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.team.TeamCreationDto;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.ProjectTypeDtoMapper;
import ru.sfedu.teamselection.mapper.TechnologyDtoMapper;
import ru.sfedu.teamselection.repository.TrackRepository;

@Component
@RequiredArgsConstructor
public class TeamCreationDtoMapper implements DtoMapper<TeamCreationDto, Team> {
    private final TechnologyDtoMapper technologyDtoMapper;
    private final ProjectTypeDtoMapper projectTypeDtoMapper;

    private final EntityManager entityManager;
    private final TrackRepository trackRepository;

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
                .currentTrack(trackRepository.findById(dto.getCurrentTrackId()).orElseThrow())
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
