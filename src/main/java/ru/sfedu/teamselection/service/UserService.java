package ru.sfedu.teamselection.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import ru.sfedu.teamselection.domain.*;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.dto.UserSearchCriteria;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.user.UserMapper;
import ru.sfedu.teamselection.repository.RoleRepository;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.UserRepository;
import ru.sfedu.teamselection.repository.specification.UserSpecification;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    private final StudentRepository studentRepository;

    @Lazy
    @Autowired
    private TeamService teamService;

    @Autowired
    private TrackService trackService;


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
    public User createOrUpdate(UserDto dto) {
        if (dto.getId() != null) {
            // --- обновление ---
            User existing = findByIdOrElseThrow(dto.getId());

            // обновляем роль
            Role role = roleRepository.findByName(dto.getRole())
                    .orElseThrow(() -> new NotFoundException("Роль '" + dto.getRole() + "' не найдена"));
            existing.setRole(role);

            // сохраняем старый и новый ID команды
            Long oldTeamId = existing.getStudent() != null && existing.getStudent().getCurrentTeam() != null
                    ? existing.getStudent().getCurrentTeam().getId()
                    : null;
            Long newTeamId = dto.getStudent() != null
                    ? dto.getStudent().getCurrentTeamId()
                    : null;

            // если команда изменилась — сначала удалить из старой, потом добавить в новую
            if (!Objects.equals(oldTeamId, newTeamId)) {
                // удаляем из старой
                if (oldTeamId != null) {
                    Team oldTeam = teamService.findByIdOrElseThrow(oldTeamId);
                    Student student = existing.getStudent();
                    teamService.removeStudentFromTeam(oldTeam, student);
                }
                // добавляем в новую
                if (newTeamId != null) {
                    Team newTeam = teamService.findByIdOrElseThrow(newTeamId);
                    Student student = existing.getStudent();
                    teamService.addStudentToTeam(newTeam, student, false);
                }
            }

            Long oldTrackId = existing.getStudent() != null && existing.getStudent().getCurrentTrack() != null
                    ? existing.getStudent().getCurrentTrack().getId()
                    : null;
            Long newTrackId = dto.getStudent() != null
                    ? dto.getStudent().getCurrentTrackId()
                    : null;

            if (!Objects.equals(oldTrackId, newTrackId)) {
                if (newTrackId != null) {
                    Track newTrack = trackService.findByIdOrElseThrow(newTrackId);
                    existing.getStudent().setCurrentTrack(newTrack);
                }
            }

            // обновляем остальные поля
            existing.setFio(dto.getFio());
            existing.setEmail(dto.getEmail());
            existing.setIsEnabled(dto.getIsEnabled());
            existing.setIsRemindEnabled(dto.getIsRemindEnabled());
            existing.getStudent().setCourse(dto.getStudent().getCourse());
            existing.getStudent().setGroupNumber(dto.getStudent().getGroupNumber());

            return userRepository.save(existing);

        } else {
            User user = userMapper.mapToEntity(dto);

            Role role = roleRepository.findByName(dto.getRole())
                    .orElseThrow(() -> new NotFoundException("Роль '" + dto.getRole() + "' не найдена"));
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
