package ru.sfedu.teamselection.repository.specification;

import java.util.List;
import java.util.Locale;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Technology;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
public final class TeamSpecification {

    private TeamSpecification() {}

    public static Specification<Team> like(String text) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        ("%" + text + "%").toLowerCase(Locale.ROOT)
                );
    }

    public static Specification<Team> byTrack(Long trackId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("currentTrack").get("id"), trackId);
    }

    public static Specification<Team> byIsFull(Boolean isFull) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("isFull"),
                        isFull
                );
    }

    public static Specification<Team> byProjectType(String projectType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("projectType").get("name")),
                        projectType.toLowerCase()
                );
    }

    public static Specification<Team> byTechnologies(List<Long> technologies) {
        return (root, cq, cb) -> {
            if (technologies == null || technologies.isEmpty()) {
                return cb.conjunction();
            }
            Join<Team, Technology> join = root.join("technologies", JoinType.LEFT);
            return join.get("id").in(technologies);
        };
    }

}
