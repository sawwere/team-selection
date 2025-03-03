package ru.sfedu.teamselection.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.dto.RoleDto;
import ru.sfedu.teamselection.mapper.user.RoleMapper;

class RoleDtoMapperTest {

    private final RoleMapper underTest = RoleMapper.INSTANCE;

    @Test
    void mapToEntity() {
        Role expected = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        RoleDto dto = RoleDto.builder()
                .id(1L)
                .name("USER")
                .build();

        Role actual = underTest.mapToEntity(dto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void mapToDto() {
        RoleDto expected = RoleDto.builder()
                .id(1L)
                .name("USER")
                .build();

        Role entity = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        RoleDto actual = underTest.mapToDto(entity);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
    }
}