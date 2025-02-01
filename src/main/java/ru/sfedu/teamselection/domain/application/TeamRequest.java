package ru.sfedu.teamselection.domain.application;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@DiscriminatorValue("request")
public class TeamRequest extends Application {

    public TeamRequest() {
        this.setType(ApplicationType.REQUEST);
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.REQUEST;
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
        return getStudent().getId();
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
        return getTeam().getCaptainId();
    }
}
