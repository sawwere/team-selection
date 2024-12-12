package ru.sfedu.teamselection.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sfedu.teamselection.domain.Technology;

public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    Technology findByName(String name);

    @Query("select t from Technology t where t.id in ?1")
    List<Technology> findAllByIdIn(List<Long> ids);
}
