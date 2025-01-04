package ru.sfedu.teamselection.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.ProjectType;
import ru.sfedu.teamselection.dto.team.ProjectTypeDto;

@Component
public class ProjectTypeDtoMapper implements DtoMapper<ProjectTypeDto, ProjectType> {
    /**
     * Map Dto to Entity
     *
     * @param dto Dto object to be mapped
     * @return mapped entity
     */
    @Override
    public ProjectType mapToEntity(ProjectTypeDto dto) {
        return ProjectType.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    /**
     * Map Entity to Dto
     *
     * @param entity Entity object to be mapped
     * @return mapped dto
     */
    @Override
    public ProjectTypeDto mapToDto(ProjectType entity) {
        return ProjectTypeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<ProjectType> mapListToEntity(List<ProjectTypeDto> dtoList) {
        List<ProjectType> result = new ArrayList<>();
        for (ProjectTypeDto item : dtoList) {
            result.add(mapToEntity(item));
        }
        return  result;
    }

    public List<ProjectTypeDto> mapListToDto(List<ProjectType> entitiesList) {
        List<ProjectTypeDto> result = new ArrayList<>();
        for (ProjectType item : entitiesList) {
            result.add(mapToDto(item));
        }
        return  result;
    }
}
