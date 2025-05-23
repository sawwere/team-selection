package ru.sfedu.teamselection.mapper.team;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.dto.team.TeamDto;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.ProjectTypeMapper;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.mapper.application.ApplicationMapper;
import ru.sfedu.teamselection.mapper.student.StudentDtoMapper;

@Component
@RequiredArgsConstructor
public class TeamDtoMapper implements DtoMapper<TeamDto, Team> {
    @Lazy
    @Autowired
    private StudentDtoMapper studentDtoMapper;

    private final TechnologyMapper technologyDtoMapper;
    private final ApplicationMapper applicationMapper;
    private final ProjectTypeMapper projectTypeDtoMapper;

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
                .projectType(projectTypeDtoMapper.mapToEntity(dto.getProjectType()))
                .quantityOfStudents(dto.getQuantityOfStudents())
                .captainId(dto.getCaptain().getId())
                .isFull(dto.getIsFull())
                .technologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()))
                .currentTrack(entityManager.getReference(Track.class, dto.getCurrentTrackId()))
                .students(dto.getStudents().stream().map(studentDtoMapper::mapToEntity).toList())
                .applications(dto.getApplications().stream().map(applicationMapper::mapToEntity).toList())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TeamDto mapToDto(Team entity) {
        if (entity == null) {
            return null;
        }
        var captain = entityManager.find(Student.class, entity.getCaptainId());

        return TeamDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .projectDescription(entity.getProjectDescription())
                .projectType(projectTypeDtoMapper.mapToDto(entity.getProjectType()))
                .quantityOfStudents(entity.getQuantityOfStudents())
                .captain(studentDtoMapper.mapToDtoWithoutTeam(captain))
                .isFull(entity.getIsFull())
                .technologies(technologyDtoMapper.mapListToDto(entity.getTechnologies()))
                .applications(entity.getApplications().stream().map(applicationMapper::mapToDto).toList())
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
                .projectType(projectTypeDtoMapper.mapToDto(entity.getProjectType()))
                .quantityOfStudents(entity.getQuantityOfStudents())
                .isFull(entity.getIsFull())
                .technologies(technologyDtoMapper.mapListToDto(entity.getTechnologies()))
                .applications(entity.getApplications().stream().map(applicationMapper::mapToDto).toList())
                .currentTrackId(entity.getCurrentTrack().getId())
                .build();
    }
}
