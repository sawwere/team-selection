package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sfedu.teamselection.domain.Technology;

public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    Technology findByName(String name);
}
