package ru.sfedu.teamselection.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.user.UserMapper;
import ru.sfedu.teamselection.repository.RoleRepository;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.UserRepository;
import ru.sfedu.teamselection.service.security.OidcUserImpl;


@RequiredArgsConstructor
@Service
public class UserService {
    private final EntityManager entityManager;
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    private final StudentRepository studentRepository;


    /**
     * Find User entity by id
     * @param id user id
     * @return user with given id
     * @throws NotFoundException in case there is no user with such id
     */
    public User findByIdOrElseThrow(Long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByFio(username);
    }

    /**
     * Get current user based on security context
     * @return Authenticated user object
     */
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OidcUserImpl oidcUser) {
            return userRepository.findByEmail(oidcUser.getEmail());
        }
        return findByUsername(username);
    }

    @Transactional
    public User createOrUpdate(UserDto userDto) {
        User user = userMapper.mapToEntity(userDto);
        if (userDto.getId() != null) {
            user = entityManager.getReference(User.class, userDto.getId());
            user.setFio(userDto.getFio());
            user.setEmail(userDto.getEmail());
            user.setIsRemindEnabled(userDto.getIsRemindEnabled());
            user.setIsEnabled(userDto.getIsEnabled());
        }
        userRepository.save(user);
        return user;
    }

    @Transactional
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public User assignRole(Long userId, String roleName) {
        User user = findByIdOrElseThrow(userId);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        if (roleName.equals("STUDENT")) {
            Student student = Student.builder()
                    .user(user)
                    .build();
            studentRepository.save(student);
        }

        user.setRole(role);
        return userRepository.save(user);
    }

}
