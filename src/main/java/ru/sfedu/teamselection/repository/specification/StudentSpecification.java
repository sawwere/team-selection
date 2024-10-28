package ru.sfedu.teamselection.repository.specification;


import org.springframework.data.jpa.domain.Specification;
import ru.sfedu.teamselection.domain.Student;

public final class StudentSpecification {
    private StudentSpecification() {}

    public static Specification<Student> like(String text) {
        return (root, query, criteriaBuilder) ->
           criteriaBuilder.like(
                   root.get("fio"),
                   text
           );
    }

    public static Specification<Student> byEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("email"),
                        email
                );
    }
}
