package ru.sfedu.teamselection.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sfedu.teamselection.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
