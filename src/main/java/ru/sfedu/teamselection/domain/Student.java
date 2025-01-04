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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity for students
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer course;

    @Column(name = "group_number")
    private Integer groupNumber;

    @Column(name = "about_self")
    @Size(max = 1024)
    private String aboutSelf;

    @Column
    @Size(max = 255)
    private String contacts;

    @Column(name = "has_team", nullable = false)
    @Builder.Default
    private Boolean hasTeam = false;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Team currentTeam;

    @Column(name = "is_captain", nullable = false)
    @Builder.Default
    private Boolean isCaptain = false;

    @Column
    @ManyToMany
    @JoinTable(
            name = "students_technologies",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    @Builder.Default
    private List<Technology> technologies = new ArrayList<>();

    @Column
    @OneToMany(mappedBy = "student")
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @OneToOne(cascade = {CascadeType.MERGE})
    private User user;
}
