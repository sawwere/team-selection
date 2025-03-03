package ru.sfedu.teamselection.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.exception.RoleNotFoundException;
import ru.sfedu.teamselection.repository.RoleRepository;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Autowired
    protected RoleRepository roleRepository;

    @Mapping(target = "role", qualifiedByName = "mapRole")
    @Mapping(target = "isEnabled", ignore = true)
    public abstract User mapToEntity(UserDto userDto);

    @Mapping(target = "role", qualifiedByName = "mapRoleToString")
    @Mapping(target = "isEnabled", ignore = true)
    public abstract UserDto mapToDto(User user);

    @Named("mapRole")
    protected Role mapRoleNameToRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));
    }

    @Named("mapRoleToString")
    protected String mapRole(Role role) {
        return role.getName();
    }
}
