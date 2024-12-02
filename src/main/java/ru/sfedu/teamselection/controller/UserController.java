package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.mapper.UserDtoMapper;
import ru.sfedu.teamselection.service.UserService;

@RestController
@RequestMapping()
@Tag(name = "UserController", description = "API для работы со пользователями")
@RequiredArgsConstructor
public class UserController {
    public static final String CURRENT_USER = "/api/v1/users/me";
    public static final String PUT_USER = "/api/v1/users";

    private final UserService userService;

    private final UserDtoMapper userDtoMapper;

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
}
