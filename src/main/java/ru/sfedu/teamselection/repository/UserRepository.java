package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.enums.Roles;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.role = ?1 where u.email = ?2")
    void updateRoleByEmail(Role role, String email);

    @Query("select u from User u join fetch u.role where u.email = ?1")
    Optional<User> findByEmailFetchRole(String email);
}

