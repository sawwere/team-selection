package ru.sfedu.teamselection.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sfedu.teamselection.domain.Student;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

//    Student findByEmail(String email);
//
//    List<Student> findAllByCaptainAndTrackId(Boolean flag, Long id);
//
//    List<Student> findStudentByStatusAndTrackId(Boolean flag, Long trackId);
//
//    List<Student> findStudentByTagsInAndTrackId(List<String> tags, Long trackId);
//
//    List<Student> findAllByIdIn(List<Long> ids);
//
//    List<Student> findAllByTrackId(Long id);
}
