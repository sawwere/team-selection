package ru.sfedu.teamselection.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tracks")
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(max = 255)
    private String name;

    @Column
    @Size(max = 255)
    private String about;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column
    private String type; //два значения bachelor/master

    @Column(name = "min_constraint")
    @Builder.Default
    private Integer minConstraint = 3;

    @Column(name = "max_constraint")
    @Builder.Default
    private Integer maxConstraint = 5;

    @Column(name = "max_second_course_constraint")
    private Integer maxSecondCourseConstraint;

    @OneToMany(mappedBy = "currentTrack", fetch = FetchType.LAZY)
    @Builder.Default
    List<Team> currentTeams = new ArrayList<>();
}
