package ru.sfedu.teamselection.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JsonProperty
    private String fio;

    @Column
    @JsonProperty
    private String email;

    @Column
    @JsonProperty
    private Integer course;

    @Column(name = "group_number")
    @JsonProperty
    private Integer groupNumber;

    @Column(name = "about_self")
    @JsonProperty
    private String aboutSelf;

    @Column
    @JsonProperty
    private String tags; // TODO Строка разделенная пробелами

    @Column
    @JsonProperty
    private String contacts;

    @Column
    @JsonProperty
    @Builder.Default
    private Boolean status = null; // статус участик команды/не участник

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Team currentTeam;

    @Column(name = "track_id")
    private Long trackId; //TODO

    @Column
    @Builder.Default
    @JsonProperty
    private Boolean captain = null; //TODO поменять логику начального значения

    @Column
    @JsonProperty
    @Builder.Default
    private String subscriptions = ""; //TODO ??

    @OneToOne
    @JsonProperty
    private User user;
}
