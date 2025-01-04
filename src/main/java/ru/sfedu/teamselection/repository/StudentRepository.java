package ru.sfedu.teamselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sfedu.teamselection.domain.Student;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Student findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
