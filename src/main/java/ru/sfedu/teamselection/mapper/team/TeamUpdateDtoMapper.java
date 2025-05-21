package ru.sfedu.teamselection.mapper.team;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.team.TeamUpdateDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamUpdateDtoMapper {
    @Mapping(source = "projectDescription", target = "projectDescription")
    @Mapping(source = "projectType",       target = "projectType")
    @Mapping(source = "technologies",      target = "technologies")
    @Mapping(source = "currentTrackId",    target = "currentTrack.id")
    Team toEntity(TeamUpdateDto dto);
}


