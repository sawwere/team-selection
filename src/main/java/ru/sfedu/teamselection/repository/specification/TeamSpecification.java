package ru.sfedu.teamselection.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;

public final class TeamSpecification {

    private TeamSpecification(){}

    public static Specification<Team> like(String text) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        root.get("name"),
                        text
                );
    }


}
