package ru.sfedu.teamselection.mapper;

import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.dto.TechnologyDto;

@Component
public class TechnologyDtoMapper implements DtoMapper<TechnologyDto, Technology> {
    /**
     * Map Dto to Entity
     *
     * @param dto Dto object to be mapped
     * @return mapped entity
     */
    @Override
    public Technology mapToEntity(TechnologyDto dto) {
        return Technology.builder()
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
    public TechnologyDto mapToDto(Technology entity) {
        return TechnologyDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
