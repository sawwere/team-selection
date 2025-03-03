package ru.sfedu.teamselection.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.sfedu.teamselection.domain.Technology;
import ru.sfedu.teamselection.dto.TechnologyDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TechnologyMapper extends DtoMapper<TechnologyDto, Technology>,
        DtoListMapper<TechnologyDto, Technology> {

    TechnologyMapper INSTANCE = Mappers.getMapper(TechnologyMapper.class);
}
