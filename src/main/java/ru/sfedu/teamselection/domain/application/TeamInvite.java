package ru.sfedu.teamselection.domain.application;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@DiscriminatorValue("invite")
public class TeamInvite extends Application {
    public TeamInvite() {
        this.setType(ApplicationType.INVITE);
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.INVITE;
    }

    /**
     * Возвращает id студента, которого можно считать отправителем для этой заявки
     * (то есть тот, кто ее отправил и может отменить).
     * Например, в случае заявки в команду это будет сам студент, отправивший заявку,
     * а в случае приглашения - капитан команды.
     * @return id студента
     */
    @Override
    public Long getSenderId() {
        return getTeam().getCaptainId();
    }

    /**
     * Возвращает id студента, являющегося целевым для этой заявки
     * (то есть тот, кто может ее принять или отклонить).
     * Например, в случае приглашения в команду это будет приглашаемый студент.
     *
     * @return id студента
     */
    @Override
    public Long getTargetId() {
        return getStudent().getId();
    }
}
