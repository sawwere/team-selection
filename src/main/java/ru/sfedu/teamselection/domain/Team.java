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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "project_description")
    @Size(max = 1024)
    private String projectDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProjectType projectType;

    @Column(name = "quantity_of_students")
    @Builder.Default
    private Integer quantityOfStudents = 0;

    @Column(name = "captain_id", nullable = false)
    @Builder.Default
    private Long captainId = -1L; //TODO check

    @Column(name = "is_full")
    @Builder.Default
    private Boolean isFull = false;

    @Column
    @ManyToMany
    @JoinTable(
            name = "teams_technologies",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    @Builder.Default
    private List<Technology> technologies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Track currentTrack;

    @Column
    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "teams_students",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students;

    @Column
    @OneToMany(mappedBy = "team")
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime  updatedAt;
}
