package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.RoleDto;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.mapper.user.RoleMapper;
import ru.sfedu.teamselection.mapper.user.UserMapper;
import ru.sfedu.teamselection.service.UserService;

@RestController
@RequestMapping()
@Tag(name = "UserController", description = "API для работы со пользователями")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    public static final String CURRENT_USER = "/api/v1/users/me";
    public static final String PUT_USER = "/api/v1/users";
    public static final String GET_ROLES = "/api/v1/roles";
    public static final String GRANT_ROLE = "/api/v1/users/{id}/assign-role";

    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleMapper roleDtoMapper;


    @Operation(
            method = "PUT",
            summary = "Изменить данные пользователя",
            description = """
                Используется для модификации данных определенного пользователя.

                Эта операция может быть выполнена самим пользователем (редактирование информации о себе),
                либо администратором ресурса.

                Недоступные для обновления поля будут проигнорированы.

                ВНИМАНИЕ: Список обновляемых полей отличается от того, кто отправил запрос -
                администратору доступны для изменения все поля, включая те, что зависят от состояния других таблиц,
                поэтому редактировать их нужно ОСТОРОЖНО.
                """,
            tags = {"UNSAFE"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность пользователя"
            ))
    @PreAuthorize("hasAuthority('ADMIN') or @userService.getCurrentUser().getId().equals(#userDto.getId())")
    @PutMapping(value = PUT_USER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto putUser(@RequestBody @Valid UserDto userDto) {
        return userMapper.mapToDto(userService.createOrUpdate(userDto));
    }

    /**
     * Возвращает текущего пользователя.
     * @return информация о текущем пользователе.
     */
    @Operation(
            method = "GET",
            summary = "Получение текущего пользователя"
    )
    @GetMapping(CURRENT_USER)
    public UserDto getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return userMapper.mapToDto(currentUser);
    }

    /**
     * Возвращает список всех возможных ролей пользователей.
     * @return новый список
     */
    @Operation(
            method = "GET",
            summary = "Получение списка всех возможных ролей"
    )
    @GetMapping(GET_ROLES)
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles()
                .stream()
                .map(roleDtoMapper::mapToDto)
                .collect(Collectors.toList())
        );
    }

    /**
     * Устанавливает заданному пользователю новую роль.
     * @param id Id пользователя
     * @param roleDto dto с информацией о новой роли
     */
    @Operation(
            method = "PUT",
            summary = "Изменить роль пользователя",
            description = """
                Используется для изменения роли пользователя.

                Эта операция может быть выполнена только администратором ресурса.
                """,
            tags = {"ADMIN"},
            parameters = {
                    @Parameter(name = "id", description = "Id пользователя", in = ParameterIn.PATH)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность роли пользователя"
            ))
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(GRANT_ROLE)
    public ResponseEntity<?> assignRole(@PathVariable Long id, @RequestBody RoleDto roleDto) {
        userService.assignRole(id, roleDto.getName());
        return ResponseEntity.ok().build();
    }
}
