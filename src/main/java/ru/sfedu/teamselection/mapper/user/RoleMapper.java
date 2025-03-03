package ru.sfedu.teamselection.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.dto.RoleDto;
import ru.sfedu.teamselection.mapper.DtoMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper extends DtoMapper<RoleDto, Role> {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    Role mapToEntity(RoleDto roleDto);

    RoleDto mapToDto(Role role);
}
