package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.TeamDto;

@Component
@RequiredArgsConstructor
public class TeamDtoMapper implements DtoMapper<TeamDto, Team> {
    private final StudentDtoMapper studentDtoMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Team mapToEntity(TeamDto dto) {
        return Team.builder()
                .id(dto.getId())
                .name(dto.getName())
                .projectDescription(dto.getAbout())
                .projectType(dto.getProjectType())
                .quantityOfStudents(dto.getQuantityOfStudents())
                .captainId(dto.getCaptainId())
                .isFull(dto.getIsFull())
//                .technologies(dto.getTags())
                .currentTrack(dto.getCurrentTrack())
                .students(dto.getStudents().stream().map(studentDtoMapper::mapToEntity).toList())
//                .applications(dto.getCandidates())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TeamDto mapToDto(Team entity) {
        return TeamDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .about(entity.getProjectDescription())
                .projectType(entity.getProjectType())
                .quantityOfStudents(entity.getQuantityOfStudents())
                .captainId(entity.getCaptainId())
                .isFull(entity.getIsFull())
//                .tags(entity.getTechnologies())
                .currentTrack(entity.getCurrentTrack())
                .students(entity.getStudents().stream().map(studentDtoMapper::mapToDto).toList())
//                .candidates(entity.getApplications())
                .build();
    }
}
