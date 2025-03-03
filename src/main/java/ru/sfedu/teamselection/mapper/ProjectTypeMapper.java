package ru.sfedu.teamselection.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.sfedu.teamselection.domain.ProjectType;
import ru.sfedu.teamselection.dto.team.ProjectTypeDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectTypeMapper extends DtoMapper<ProjectTypeDto, ProjectType>,
        DtoListMapper<ProjectTypeDto, ProjectType> {

    ProjectTypeMapper INSTANCE = Mappers.getMapper(ProjectTypeMapper.class);

    ProjectType mapToEntity(ProjectTypeDto projectTypeDto);

    List<ProjectTypeDto> mapListToDto(List<ProjectType> projectType);

    ProjectTypeDto mapToDto(ProjectType projectType);
}
