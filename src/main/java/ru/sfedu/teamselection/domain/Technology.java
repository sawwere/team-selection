package ru.sfedu.teamselection.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity of various technologies/skills used or required in a team project or specific traits of a student
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "technologies")
public class Technology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "students_technologies",
            joinColumns = @JoinColumn(name = "technology_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Builder.Default
    private List<Student> students = new ArrayList<>();

    @Column
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "teams_technologies",
            joinColumns = @JoinColumn(name = "technology_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    @Builder.Default
    private List<Team> teams = new ArrayList<>();
}
