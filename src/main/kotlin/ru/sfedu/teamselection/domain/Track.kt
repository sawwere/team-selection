package ru.sfedu.teamselection.domain

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.format.annotation.DateTimeFormat
import java.util.*
import jakarta.persistence.*


@Entity
@Table(name = "track")
@Access(AccessType.FIELD)
class Track(
    @Column
    @JsonProperty
    var name: String? = null,
    @Column
    @JsonProperty
    var about: String? = null,
    @Column
    @JsonProperty
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    var startDate: Date? = null,
    @Column
    @JsonProperty
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    var endDate: Date? = null,
    @Column
    @JsonProperty
    var type: String? = null, //два значения bachelor/master
    @Column
    @JsonProperty
    var minConstraint: Int? = 3,
    @Column
    @JsonProperty
    var maxConstraint: Int? = 5,
    @Column
    var maxThirdCourseConstraint: Int? = null,
    @OneToMany(mappedBy = "currentTrack", fetch = FetchType.LAZY)
    @JsonManagedReference
    var currentTeams: MutableList<Team>? = mutableListOf(),
): Domain() {
    override fun toString(): String {
        return ""
    }

    override fun equals(other: Any?): Boolean {
        return true
    }
}