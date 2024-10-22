package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.enums.Roles;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.role = :role WHERE u.email = :email")
    void updateRoleByEmail(@Param("email") String email, @Param("role") Roles role);
}

