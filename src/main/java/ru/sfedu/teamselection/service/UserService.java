package ru.sfedu.teamselection.service;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.repository.UserRepository;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

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
}
