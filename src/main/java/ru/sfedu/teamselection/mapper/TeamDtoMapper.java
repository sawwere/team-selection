package ru.sfedu.teamselection.mapper;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.dto.TeamCreationDto;
import ru.sfedu.teamselection.dto.TeamDto;

@Component
@RequiredArgsConstructor
public class TeamDtoMapper implements DtoMapper<TeamDto, Team> {
    @Lazy
    @Autowired
    private StudentDtoMapper studentDtoMapper;

    private final TechnologyDtoMapper technologyDtoMapper;
    private final ApplicationCreationDtoMapper applicationCreationDtoMapper;

    private final EntityManager entityManager;

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
                .captainId(dto.getCaptain().getId())
                .isFull(dto.getIsFull())
                .technologies(new ArrayList<>()) //TODO: map strings to technologies
                .currentTrack(entityManager.getReference(Track.class, dto.getCurrentTrackId()))
                .students(dto.getStudents().stream().map(studentDtoMapper::mapToEntity).toList())
                .applications(dto.getApplications().stream().map(applicationCreationDtoMapper::mapToEntity).toList())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TeamDto mapToDto(Team entity) {
        Optional<Student> captain = entity
                .getStudents()
                .stream()
                .filter(Student::getIsCaptain)
                .findFirst();
        return TeamDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .projectDescription(entity.getProjectDescription())
                .projectType(entity.getProjectType())
                .quantityOfStudents(entity.getQuantityOfStudents())
                .captain(captain.isEmpty() ? null : studentDtoMapper.mapToDtoWithoutTeam(captain.get()))
                .isFull(entity.getIsFull())
                .technologies(technologyDtoMapper.mapListToDto(entity.getTechnologies()))
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
                .isFull(entity.getIsFull())
                .technologies(technologyDtoMapper.mapListToDto(entity.getTechnologies()))
                .applications(entity.getApplications().stream().map(applicationCreationDtoMapper::mapToDto).toList())
                .currentTrackId(entity.getCurrentTrack().getId())
                .build();
    }

    public Team mapCreationToEntity(TeamCreationDto dto) {
        return Team.builder()
                .name(dto.getName())
                .projectDescription(dto.getProjectDescription())
                .projectType(dto.getProjectType())
                .captainId(dto.getCaptainId())
                .students(new ArrayList<>())
                .applications(new ArrayList<>())
                .technologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()))
                .currentTrack(entityManager.getReference(Track.class, dto.getCurrentTrackId()))
                .build();
    }
}
