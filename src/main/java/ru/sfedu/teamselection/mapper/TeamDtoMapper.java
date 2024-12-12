package ru.sfedu.teamselection.mapper;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.TeamCreationDto;
import ru.sfedu.teamselection.dto.TeamDto;
import ru.sfedu.teamselection.repository.TrackRepository;

@Component
@RequiredArgsConstructor
public class TeamDtoMapper implements DtoMapper<TeamDto, Team> {
    @Lazy
    @Autowired
    private StudentDtoMapper studentDtoMapper;
    private final ApplicationCreationDtoMapper applicationCreationDtoMapper;

    private final TrackRepository trackRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Team mapToEntity(TeamDto dto) {
        return Team.builder()
                .id(dto.getId())
                .name(dto.getName())
                .projectDescription(dto.getProjectDescription())
                .projectType(dto.getProjectType())
                .quantityOfStudents(dto.getQuantityOfStudents())
                .captainId(dto.getCaptainId())
                .isFull(dto.getIsFull())
                .technologies(new ArrayList<>()) //TODO: map strings to technologies
                .currentTrack(trackRepository.findById(dto.getCurrentTrackId()).orElseThrow())
                .students(dto.getStudents().stream().map(studentDtoMapper::mapToEntity).toList())
                .applications(dto.getApplications().stream().map(applicationCreationDtoMapper::mapToEntity).toList())
                .build();
    }
    public Team mapToEntityWithoutStudents(TeamDto dto) {
        return Team.builder()
                .id(dto.getId())
                .name(dto.getName())
                .projectDescription(dto.getProjectDescription())
                .projectType(dto.getProjectType())
                .quantityOfStudents(dto.getQuantityOfStudents())
                .captainId(dto.getCaptainId())
                .isFull(dto.getIsFull())
                .technologies(new ArrayList<>()) //TODO: map strings to technologies
                .currentTrack(trackRepository.findById(dto.getCurrentTrackId()).orElseThrow())
                .applications(dto.getApplications().stream().map(applicationCreationDtoMapper::mapToEntity).toList())
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
                .projectDescription(entity.getProjectDescription())
                .projectType(entity.getProjectType())
                .quantityOfStudents(entity.getQuantityOfStudents())
                .captainId(entity.getCaptainId())
                .isFull(entity.getIsFull())
                .applications(entity.getApplications().stream().map(applicationCreationDtoMapper::mapToDto).toList())
                .currentTrackId(entity.getCurrentTrack().getId())
                .students(entity.getStudents().stream().map(studentDtoMapper::mapToDtoWithoutTeam).toList())
                .build();
    }
    public TeamDto mapToDtoWithoutStudents(Team entity) {
        if (entity == null) {
            return null;
        }
        return TeamDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .projectDescription(entity.getProjectDescription())
                .projectType(entity.getProjectType())
                .quantityOfStudents(entity.getQuantityOfStudents())
                .captainId(entity.getCaptainId())
                .isFull(entity.getIsFull())
                .applications(entity.getApplications().stream().map(applicationCreationDtoMapper::mapToDto).toList())
                .currentTrackId(entity.getCurrentTrack().getId())
                .build();
    }

    public Team mapCreationToEntity(TeamCreationDto dto) {
        return Team.builder()
                .name(dto.getName())
                .projectDescription(dto.getAbout())
                .projectType(dto.getProjectType())
                .captainId(dto.getCaptainId())
                .students(new ArrayList<>())
                .applications(new ArrayList<>())
                .technologies(new ArrayList<>()) //TODO: map strings to technologies
                .currentTrack(trackRepository.findById(dto.getCurrentTrackId()).orElseThrow())
                .build();
    }
}
