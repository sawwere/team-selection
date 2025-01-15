package ru.sfedu.teamselection.mapper.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.dto.RoleDto;
import ru.sfedu.teamselection.mapper.DtoMapper;

@Component
@RequiredArgsConstructor
public class RoleDtoMapper implements DtoMapper<RoleDto, Role> {

    @Override
    public Role mapToEntity(RoleDto dto) {
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName()).build();
    }

    @Override
    public RoleDto mapToDto(Role entity) {
        return RoleDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
