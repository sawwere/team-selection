package ru.sfedu.teamselection.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
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
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.dto.UserSearchCriteria;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.user.UserMapper;
import ru.sfedu.teamselection.repository.RoleRepository;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.UserRepository;
import ru.sfedu.teamselection.repository.specification.UserSpecification;
import ru.sfedu.teamselection.service.security.OidcUserImpl;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    private final StudentRepository studentRepository;

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
                .orElseThrow(() -> new NotFoundException(id.toString()));
    }

    public List<User> findAllUsers()
    {
        return userRepository.findAll();
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
            throw new NotFoundException("User with username "+username);
        }
        return u;
    }

    /**
     * Get current user based on security context
     * @return Authenticated user object
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principalName = auth.getName();
        var r = auth.getAuthorities();
        if (auth.getPrincipal() instanceof OidcUser oidc) {
            return findByEmail(oidc.getEmail());
        } else {
            return findByUsername(principalName);
        }
    }

    @Transactional
    public User createOrUpdate(UserDto dto) {
        if (dto.getId() != null) {
            User existing = findByIdOrElseThrow(dto.getId());

            Optional<Role> roleOpt = roleRepository.findByName(dto.getRole());
            if (roleOpt.isPresent()) {
                existing.setRole(roleOpt.get());
            } else {
                throw new NotFoundException("Роль '" + dto.getRole() + "' не найдена");
            }

            existing.setFio(dto.getFio());
            existing.setEmail(dto.getEmail());
            existing.setIsEnabled(dto.getIsEnabled());
            existing.setIsRemindEnabled(dto.getIsRemindEnabled());

            return userRepository.save(existing);
        } else {
            User user = userMapper.mapToEntity(dto);

            Optional<Role> roleOpt = roleRepository.findByName(dto.getRole());
            if (roleOpt.isPresent()) {
                user.setRole(roleOpt.get());
            } else {
                throw new NotFoundException("Роль '" + dto.getRole() + "' не найдена");
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
                .orElseThrow(() -> new NotFoundException("Role "+roleName));

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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        user.setIsEnabled(false);
        userRepository.save(user);
    }

}
