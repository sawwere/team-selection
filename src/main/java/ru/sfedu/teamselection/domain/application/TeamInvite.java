package ru.sfedu.teamselection.domain.application;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;

@SuperBuilder
@Entity
@DiscriminatorValue("invite")
public class TeamInvite extends Application {
    public TeamInvite(Long id, Student student, Team team, String status) {
        super(id, student, team, status, ApplicationType.INVITE);
    }

    public TeamInvite() {
        this.setType(ApplicationType.INVITE);
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.INVITE;
    }

    @Override
    public Long getSenderId() {
        return getTeam().getCaptainId();
    }
}
