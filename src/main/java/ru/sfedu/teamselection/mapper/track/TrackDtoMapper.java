package ru.sfedu.teamselection.mapper.track;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.dto.track.TrackDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamDtoMapper;

@Component
@RequiredArgsConstructor
public class TrackDtoMapper implements DtoMapper<TrackDto, Track> {

    @Autowired
    private TeamDtoMapper teamDtoMapper;


    @Override
    public Track mapToEntity(TrackDto dto) {
        return Track.builder()
                .id(dto.getId())
                .name(dto.getName())
                .about(dto.getAbout())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .type(TrackType.valueOf(dto.getType()))
                .minConstraint(dto.getMinConstraint())
                .maxConstraint(dto.getMaxConstraint())
                .maxSecondCourseConstraint(dto.getMaxSecondCourseConstraint())
                .currentTeams(dto.getCurrentTeams().stream().map(x->teamDtoMapper.mapToEntity(x)).toList())
                .build();
    }

    @Override
    public TrackDto mapToDto(Track entity) {
        return TrackDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .about(entity.getAbout())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .minConstraint(entity.getMinConstraint())
                .maxConstraint(entity.getMaxConstraint())
                .maxSecondCourseConstraint(entity.getMaxSecondCourseConstraint())
                .currentTeams(entity.getCurrentTeams().stream().map(x->teamDtoMapper.mapToDto(x)).toList())
                .build();
    }
}

