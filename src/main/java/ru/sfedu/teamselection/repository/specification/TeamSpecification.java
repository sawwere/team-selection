package ru.sfedu.teamselection.repository.specification;

import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import ru.sfedu.teamselection.domain.Team;

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
                        root.get("projectType"),
                        projectType
                );
    }

    public static Specification<Team> byTechnologies(List<Long> technologyIds) {
        return (root, query, criteriaBuilder) -> {
            if (technologyIds == null || technologyIds.isEmpty()) {
                return criteriaBuilder.conjunction(); // do not filter if there is no items in the list
            }

            return root.join("technologies")
                    .get("id")
                    .in(technologyIds);
        };
    }
}
