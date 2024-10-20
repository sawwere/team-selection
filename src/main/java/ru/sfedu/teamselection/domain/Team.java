package ru.sfedu.teamselection.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



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

    @Column
    private String about;

    @Column(name = "project_type")
    private String projectType;

    @Column(name = "quantity_of_students")
    @Builder.Default
    private Integer quantityOfStudents = 0;

    @Column(name = "captain_id")
    @Builder.Default
    private Long captainId = 0L; //TODO check

    @Column(name = "is_full")
    @Builder.Default
    private Boolean fullFlag = false; //TODO rename

    @Column
    @Builder.Default
    private String tags = ""; //TODO

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Track currentTrack;

    @Column
    @OneToMany(mappedBy = "currentTeam", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Student> students;

    @Column
    @Builder.Default
    private String candidates = ""; //TODO
}
