package ru.sfedu.teamselection.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.dto.UserSearchCriteria;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.UserToStudentUpdateMapper;
import ru.sfedu.teamselection.mapper.user.UserMapper;
import ru.sfedu.teamselection.repository.RoleRepository;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.UserRepository;
import ru.sfedu.teamselection.repository.specification.UserSpecification;
import ru.sfedu.teamselection.service.security.PermissionLevelUpdate;
import ru.sfedu.teamselection.service.student.update.StudentUpdateFactory;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;

    private final StudentUpdateFactory studentUpdateFactory;
    private final UserToStudentUpdateMapper userToStudentUpdateMapper;
    @Lazy
    @Autowired
    private TeamService teamService;

    @Autowired
    private TrackService trackService;

    private final UserMapper userMapper;


    @Transactional(readOnly = true)
    public Page<UserDto> search(UserSearchCriteria criteria, Pageable pageable) {
        Specification<User> spec = UserSpecification.build(criteria);
        return userRepository.findAll(spec, pageable)
                .map(userMapper::mapToDto);
    }


    /**
     * Find User entity by id
     * @param id user id
     * @return user with given id
     * @throws NotFoundException in case there is no user with such id
     */
    public User findByIdOrElseThrow(Long id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public User findByEmail(String email) {
        User u = userRepository.findByEmail(email);
        if (u == null) {
            throw new NotFoundException(email);
        }
        return u;
    }

    public User findByUsername(String username) {
        User u = userRepository.findByFio(username);
        if (u == null) {
            throw new NotFoundException("User with username " + username);
        }
        return u;
    }

    public Role findRoleByNameOrElseThrow(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Роль '" + roleName + "' не найдена"));
    }

    /**
     * Get current user based on security context
     * @return Authenticated user object
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principalName = auth.getName();
        var authAuthorities = auth.getAuthorities();
        if (auth.getPrincipal() instanceof OidcUser oidc) {
            return findByEmail(oidc.getEmail());
        } else {
            return findByUsername(principalName);
        }
    }

    @Transactional
    public User createOrUpdate(UserDto dto, PermissionLevelUpdate permission) {
        if (dto.getId() != null) {
            // --- обновление ---
            User existing = findByIdOrElseThrow(dto.getId());

            // обновляем роль
            Role role = findRoleByNameOrElseThrow(dto.getRole())
            existing.setRole(role);

            // обновляем остальные поля
            existing.setFio(dto.getFio());
            existing.setEmail(dto.getEmail());
            existing.setIsEnabled(dto.getIsEnabled());
            existing.setIsRemindEnabled(dto.getIsRemindEnabled());
            if (existing.getStudent() != null) {
                studentUpdateFactory.getHandler(permission).update(
                        existing.getStudent(),
                        userToStudentUpdateMapper.userDtoToStudentUpdateDto(dto)
                );
            }

            return userRepository.save(existing);

        } else {
            User user = userMapper.mapToEntity(dto);

            Role role = findRoleByNameOrElseThrow(dto.getRole());
            user.setRole(role);
            if (dto.getStudent() != null) {
                Student student = Student.builder()
                        .user(user)
                        .build();
                studentRepository.save(student);

                Long teamId = dto.getStudent().getCurrentTeamId();
                if (teamId != null) {
                    Team team = teamService.findByIdOrElseThrow(teamId);
                    teamService.addStudentToTeam(team, student, false);
                }
            }

            return userRepository.save(user);
        }
    }


    @Transactional
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public User assignRole(Long userId, String roleName) {
        User user = findByIdOrElseThrow(userId);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role " + roleName));

        if ("STUDENT".equals(roleName)) {
            Student student = Student.builder()
                    .user(user)
                    .build();
            studentRepository.save(student);
        }

        user.setRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = findByIdOrElseThrow(id);
        user.setIsEnabled(false);
        userRepository.save(user);
    }

}
