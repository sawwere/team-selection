package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.dto.RoleDto;

@Component
@RequiredArgsConstructor
public class RoleDtoMapper implements DtoMapper<RoleDto, Role> {

    @Override
    public Role mapToEntity(RoleDto dto) {
        return Role.builder().name(dto.getName()).id(dto.getId()).build();
    }

    @Override
    public RoleDto mapToDto(Role entity) {
        return RoleDto.builder().id(entity.getId()).name(entity.getName()).build();
    }
}
