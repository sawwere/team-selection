package ru.sfedu.teamselection.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.mapper.DtoMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends DtoMapper<UserDto, User> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", qualifiedByName = "mapRole")
    @Mapping(target = "isEnabled", ignore = true)
    User mapToEntity(UserDto userDto);

    @Mapping(target = "role", qualifiedByName = "mapRoleToString")
    @Mapping(target = "isEnabled", ignore = true)
    UserDto mapToDto(User user);

    @Named("mapRole")
    default Role mapRole(String roleName) {
        return Role.builder().name(roleName).build();
    }

    @Named("mapRoleToString")
    default String mapRole(Role role) {
        return role.getName();
    }
}
