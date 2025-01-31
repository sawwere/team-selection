package ru.sfedu.teamselection.domain.application;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.enums.ApplicationStatus;

/**
 * Entity of student's applications for participation in teams
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "applications")
public abstract class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Team team;

    @Builder.Default
    private String status = ApplicationStatus.SENT.name();

    @Column(insertable = false, updatable = false)
    private ApplicationType type;

    /**
     * Returns the id of the student who send this application.
     * For invite, it would be the id of the captain,
     * for request - id of the student applying for the team
     * @return the id of the student
     */
    public abstract Long getSenderId();
}
