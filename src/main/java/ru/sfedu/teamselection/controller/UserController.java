package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.RoleDto;
import ru.sfedu.teamselection.dto.StudentCreationDto;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.mapper.RoleDtoMapper;
import ru.sfedu.teamselection.mapper.UserDtoMapper;
import ru.sfedu.teamselection.service.StudentService;
import ru.sfedu.teamselection.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping()
@Tag(name = "UserController", description = "API для работы со пользователями")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    public static final String CURRENT_USER = "/api/v1/users/me";
    public static final String PUT_USER = "/api/v1/users";
    public static final String GET_ROLES = "/api/v1/roles";

    private final UserService userService;

    private final UserDtoMapper userDtoMapper;

    private final RoleDtoMapper roleDtoMapper;


    @PutMapping(PUT_USER)
    public UserDto putUser(@RequestBody @Valid UserDto userDto) {
        return userDtoMapper.mapToDto(userService.createOrUpdate(userDto));
    }

    /**
     * Получение текущего пользователя.
     * @return информация о текущем пользователе.
     */
    @GetMapping(CURRENT_USER)
    public ResponseEntity<UserDto> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        UserDto userDto = UserDto.builder()
                .id(currentUser.getId())
                .fio(currentUser.getFio())
                .email(currentUser.getEmail())
                .role(currentUser.getRole().getName())
                .isRemindEnabled(currentUser.getIsRemindEnabled())
                .build();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping(GET_ROLES)
    public ResponseEntity<List<RoleDto>> getAllRoles()
    {
        return ResponseEntity.ok(userService.getAllRoles().stream().map(roleDtoMapper::mapToDto).collect(Collectors.toList()));
    }

    @PostMapping("/api/v1/users/{id}/assign-role")
    public ResponseEntity<?> assignRole(@PathVariable Long id, @RequestBody RoleDto roleDto) {
        userService.assignRole(id, roleDto.getName());
        return ResponseEntity.ok().build();
    }
}
