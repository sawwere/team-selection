package ru.sfedu.teamselection.mapper.track;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.dto.track.TrackCreationDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.mapper.DtoMapper;

@Component
@RequiredArgsConstructor
public class TrackCreationDtoMapper implements DtoMapper<TrackCreationDto, Track> {


    @Override
    public Track mapToEntity(TrackCreationDto dto) {
        return Track.builder()
                .id(dto.getId())
                .name(dto.getName())
                .about(dto.getAbout())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .type(TrackType.valueOf(dto.getType())) // Преобразование строки в Enum
                .minConstraint(dto.getMinConstraint())
                .maxConstraint(dto.getMaxConstraint())
                .maxSecondCourseConstraint(dto.getMaxSecondCourseConstraint())
                .build();
    }

    @Override
    public TrackCreationDto mapToDto(Track entity) {
        return TrackCreationDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .about(entity.getAbout())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .type(entity.getType() != null ? entity.getType().name() : null) // Преобразование Enum в строку
                .minConstraint(entity.getMinConstraint())
                .maxConstraint(entity.getMaxConstraint())
                .maxSecondCourseConstraint(entity.getMaxSecondCourseConstraint())
                .build();
    }
}
