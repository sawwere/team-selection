package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.repository.RoleRepository;

@Component
@RequiredArgsConstructor
public class UserDtoMapper implements DtoMapper<UserDto, User> {
    private final RoleRepository roleRepository;

    /**
     * Map Dto to Entity
     *
     * @param dto Dto object to be mapped
     * @return mapped entity
     */
    @Override
    public User mapToEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .fio(dto.getFio())
                .email(dto.getEmail())
                .role(roleRepository.findByName(dto.getRole()).orElseThrow())
                .build();
    }

    /**
     * Map Entity to Dto
     *
     * @param entity Entity object to be mapped
     * @return mapped dto
     */
    @Override
    public UserDto mapToDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .fio(entity.getFio())
                .email(entity.getEmail())
                .role(entity.getRole().getAuthority())
                .build();
    }
}
