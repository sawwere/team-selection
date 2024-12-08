package ru.sfedu.teamselection.service;

import jakarta.persistence.EntityManager;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.mapper.UserDtoMapper;
import ru.sfedu.teamselection.repository.UserRepository;


@RequiredArgsConstructor
@Service
public class UserService {
    private final EntityManager entityManager;
    private final UserRepository userRepository;

    private final UserDtoMapper userDtoMapper;

    /**
     * Find User entity by id
     * @param id user id
     * @return user with given id
     * @throws NoSuchElementException in case there is no user with such id
     */
    public User findByIdOrElseThrow(Long id) throws NoSuchElementException {
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
        return findByUsername(username);
    }

    @Transactional
    public User createOrUpdate(UserDto userDto) {
        User user = userDtoMapper.mapToEntity(userDto);
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
}
