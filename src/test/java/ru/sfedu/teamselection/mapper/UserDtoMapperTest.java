package ru.sfedu.teamselection.mapper;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.repository.RoleRepository;

class UserDtoMapperTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserDtoMapper underTest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.doReturn(
            Optional.of(Role.builder()
                    .id(1L)
                    .name("ADMIN")
                    .build())
        ).when (roleRepository).findByName(Mockito.anyString());
    }

    @Test
    void mapToEntity() {
        User expected = User.builder()
                .id(1L)
                .fio("f i o")
                .email("example@example.com")
                .role(Role.builder()
                        .id(1L)
                        .name("ADMIN")
                        .build())
                .isRemindEnabled(true)
                .build();

        UserDto dto = UserDto.builder()
                .id(1L)
                .fio("f i o")
                .email("example@example.com")
                .role("ADMIN")
                .isRemindEnabled(true)
                .build();

        User actual = underTest.mapToEntity(dto);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFio(), actual.getFio());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getRole().getName(), actual.getRole().getName());
        Assertions.assertEquals(expected.getIsEnabled(), actual.getIsEnabled());
        Assertions.assertEquals(expected.getIsRemindEnabled(), actual.getIsRemindEnabled());
    }

    @Test
    void mapToDto() {
        UserDto expected = UserDto.builder()
                .id(1L)
                .fio("f i o")
                .email("example@example.com")
                .role("ADMIN")
                .isRemindEnabled(false)
                .build();

        User entity = User.builder()
                .id(1L)
                .fio("f i o")
                .email("example@example.com")
                .role(Role.builder()
                        .id(1L)
                        .name("ADMIN")
                        .build())
                .isRemindEnabled(false)
                .build();

        UserDto actual = underTest.mapToDto(entity);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getFio(), actual.getFio());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getRole(), actual.getRole());
        Assertions.assertEquals(expected.getIsEnabled(), actual.getIsEnabled());
        Assertions.assertEquals(expected.getIsRemindEnabled(), actual.getIsRemindEnabled());
    }
}