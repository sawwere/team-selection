package ru.sfedu.teamselection.repository.specification;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserSearchCriteria;

public class UserSpecification {

    public static Specification<User> withFioLike(String fio) {
        return (root, cq, cb) ->
                fio == null
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("fio")), "%" + fio.toLowerCase() + "%");
    }

    public static Specification<User> withEmailLike(String email) {
        return (root, cq, cb) ->
                email == null
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> withRole(String role) {
        return (root, cq, cb) ->
                role == null
                        ? cb.conjunction()
                        : cb.equal(root.get("role").get("name"), role);
    }

    public static Specification<User> withCourse(Integer course) {
        return (root, cq, cb) ->
                course == null
                        ? cb.conjunction()
                        : cb.equal(root.join("student", JoinType.LEFT).get("course"), course);
    }

    public static Specification<User> withGroupNumber(Integer groupNumber) {
        return (root, cq, cb) ->
                groupNumber == null
                        ? cb.conjunction()
                        : cb.equal(root.join("student", JoinType.LEFT).get("groupNumber"), groupNumber);
    }

    public static Specification<User> byTrack(Long trackId) {
        return (root, cq, cb) ->
                trackId == null
                        ? cb.conjunction()
                        : cb.equal(
                            root.join("student", JoinType.LEFT)
                                .get("currentTrack").get("id"),
                            trackId
                        );
    }


    public static Specification<User> withEnabled(Boolean enabled) {
        return (root, cq, cb) ->
                enabled == null
                        ? cb.conjunction()
                        : cb.equal(root.get("isEnabled"), enabled);
    }

    public static Specification<User> build(UserSearchCriteria c) {
        return Specification
                .where(withEnabled(c.getIsEnabled()))
                .and(withFioLike(c.getFio()))
                .and(withEmailLike(c.getEmail()))
                .and(withRole(c.getRole()))
                .and(withCourse(c.getCourse()))
                .and(byTrack(c.getTrackId()))
                .and(withGroupNumber(c.getGroupNumber()));
    }
}
