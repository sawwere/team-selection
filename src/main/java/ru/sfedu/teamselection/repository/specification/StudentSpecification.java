package ru.sfedu.teamselection.repository.specification;


import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;

public final class StudentSpecification {
    private StudentSpecification() {}

    public static Specification<Student> like(String text) {
        return (root, query, criteriaBuilder) ->
             criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("user").get("fio")),
                    ("%" + text + "%").toLowerCase(Locale.ROOT)
            );
    }


    public static Specification<Student> byCourse(List<Integer> course) {

        return (root, query, criteriaBuilder) -> {
            if (course == null || course.isEmpty()) {
                return criteriaBuilder.conjunction(); // do not filter if list is empty
            }
            return root.get("course").in(course);
        };
        /*
        return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(
                root.get("course"),
                course
        );*/
    }

    public static Specification<Student> byTrack(Long trackId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("currentTrack").get("id"), trackId);
    }

    public static Specification<Student> byGroup(List<Integer> group) {

        return (root, query, criteriaBuilder) -> {
            if (group == null || group.isEmpty()) {
                return criteriaBuilder.conjunction(); // do not filter if list is empty
            }
            return root.get("groupNumber").in(group);
        };
        /*
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("groupNumber"),
                        group
                );*/
    }

    public static Specification<Student> byHasTeam(Boolean hasTeam) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("hasTeam"),
                        hasTeam
                );
    }

    public static Specification<Student> byIsCaptain(Boolean isCaptain) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("isCaptain"),
                        isCaptain
                );
    }

    public static Specification<Student> hasTechnologies(List<Long> technologies) {
        return (root, query, criteriaBuilder) -> {
            if (technologies == null || technologies.isEmpty()) {
                return criteriaBuilder.conjunction(); // do not filter if list is empty
            }
            return root.join("technologies")
                    .get("id")
                    .in(technologies);
        };
    }
}
