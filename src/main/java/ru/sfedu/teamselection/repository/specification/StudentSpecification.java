package ru.sfedu.teamselection.repository.specification;


import org.springframework.data.jpa.domain.Specification;
import ru.sfedu.teamselection.domain.Student;

import java.util.Locale;

public final class StudentSpecification {
    private StudentSpecification() {}

    public static Specification<Student> like(String text) {
        return (root, query, criteriaBuilder) ->
             criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("user").get("fio")),
                    ("%" + text + "%").toLowerCase(Locale.ROOT)
            );
    }


    public static Specification<Student> byCourse(Integer course) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("course"),
                        course
                );
    }

    public static Specification<Student> byGroup(Integer group) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("groupNumber"),
                        group
                );
    }

    public static Specification<Student> byHasTeam(Boolean hasTeam) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("hasTeam"),
                        hasTeam
                );
    }
}
