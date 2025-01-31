package ru.sfedu.teamselection.domain.application;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;

@SuperBuilder
@Entity
@DiscriminatorValue("request")
public class TeamRequest extends Application {

    public TeamRequest() {
        this.setType(ApplicationType.REQUEST);
    }

    public TeamRequest(Long id, Student student, Team team, String status) {
        super(id, student, team, status, ApplicationType.REQUEST);
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.REQUEST;
    }

    @Override
    public Long getSenderId() {
        return getStudent().getId();
    }
}
