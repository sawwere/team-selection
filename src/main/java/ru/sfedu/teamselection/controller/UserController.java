package ru.sfedu.teamselection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.RoleDto;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.dto.UserSearchCriteria;
import ru.sfedu.teamselection.exception.AzureException;
import ru.sfedu.teamselection.mapper.user.RoleMapper;
import ru.sfedu.teamselection.mapper.user.UserMapper;
import ru.sfedu.teamselection.service.PhotoService;
import ru.sfedu.teamselection.service.UserService;
import ru.sfedu.teamselection.service.security.PermissionLevelUpdate;

@RestController
@RequestMapping()
@Tag(name = "UserController", description = "API для работы со пользователями")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    public static final String CURRENT_USER = "/api/v1/users/me";
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static final String PUT_USER = "/api/v1/users";
    public static final String FIND_USERS = "/api/v1/users";
    public static final String DELETE_USER = "/api/v1/users/{id}";
    public static final String GET_ROLES = "/api/v1/roles";
    public static final String GRANT_ROLE = "/api/v1/users/{id}/assign-role";
    public static final String GET_USER_PHOTO = "/api/v1/users/{id}/photo";


    private final UserService userService;
    private final PhotoService photoService;

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
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userService.getCurrentUser().getId().equals(#userDto.getId())")
    @PutMapping(value = PUT_USER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Auditable(auditPoint = "User.PutUser")
    public ResponseEntity<UserDto> putUser(@RequestBody @Valid UserDto userDto) {
        User user = userService.getCurrentUser();
        var permission = user.getRole().getName().equals("ADMIN")
                ? PermissionLevelUpdate.ADMIN
                : PermissionLevelUpdate.OWNER;
        UserDto result = userMapper.mapToDto(userService.createOrUpdate(userDto, permission));
        return ResponseEntity.ok(result);
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
    @Auditable(auditPoint = "User.GetCurrentUser")
    public ResponseEntity<UserDto> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        UserDto result = userMapper.mapToDto(currentUser);
        return ResponseEntity.ok(result);
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Auditable(auditPoint = "User.GetAllRoles")
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
            parameters = {
                    @Parameter(name = "id", description = "Id пользователя", in = ParameterIn.PATH)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Сущность роли пользователя"
            ))
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(GRANT_ROLE)
    @Auditable(auditPoint = "User.AssignRole")
    public ResponseEntity<?> assignRole(@PathVariable Long id, @RequestBody RoleDto roleDto) {
        userService.assignRole(id, roleDto.getName());
        return ResponseEntity.ok().build();
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    @GetMapping(FIND_USERS)
    @Auditable(auditPoint = "User.SearchUsers")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(value = "fio",         required = false) String  fio,
            @RequestParam(value = "email",       required = false) String  email,
            @RequestParam(value = "role",        required = false) String  role,
            @RequestParam(value = "course",      required = false) Integer course,
            @RequestParam(value = "groupNumber", required = false) Integer groupNumber,
            @RequestParam(value = "trackId",     required = false) Long    trackId,
            @RequestParam(value = "isEnabled",   required = false) Boolean isEnabled,
            @RequestParam(value = "page",        defaultValue = "0")  int     page,
            @RequestParam(value = "size",        defaultValue = "15") int     size,
            @RequestParam(value = "sort",        defaultValue = "fio,asc") String sort
    ) {
        var criteria = UserSearchCriteria.builder()
                .fio(fio)
                .email(email)
                .role(role)
                .course(course)
                .groupNumber(groupNumber)
                .trackId(trackId)
                .isEnabled(isEnabled)
                .build();

        String[] parts = sort.split(",");
        Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));

        Page<UserDto> result = userService.search(criteria, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Удалить пользователя")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(DELETE_USER)
    @Auditable(auditPoint = "User.DeleteUser")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User with id: " + id + "was deleted");
    }

    @GetMapping(GET_USER_PHOTO)
    @Auditable(auditPoint = "User.GetPhoto")
    public ResponseEntity<byte[]> getPhoto(
            OAuth2AuthenticationToken authentication,
            @PathVariable(value = "id") Long id
    ) {
        byte[] photoBytes;
        try {
            photoBytes = photoService.getAzureUserPhoto(id, authentication);

        } catch (AzureException azureException) {
            photoBytes = photoService.getPlaceholder();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(photoBytes);
    }
}
