package ru.sfedu.teamselection.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.boot.context.properties.bind.DefaultValue
import jakarta.persistence.*

@Entity
@Table(name = "team")
@Access(AccessType.FIELD)
class Team(
    @Column
    var name: String? = null,
    @Column
    var about: String? = null,
    @Column
    var projectType: String? = null,
    @Column
    @DefaultValue(value = ["0"])
    var quantityOfStudents: Int? = 0,
    @Column
    var captainId: Long? = 0,
    @Column
    @DefaultValue(value = ["false"])
    var fullFlag: Boolean? = null,
    @Column
    var tags: String? = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    var currentTrack: Track? = null,
    @Column
    @OneToMany(mappedBy = "currentTeam", fetch = FetchType.LAZY)
    @JsonManagedReference
    var students: MutableList<Student>? = null,
    @Column
    var candidates: String? = ""
): Domain() {
    override fun toString(): String { //TODO fix
        return ""
    }

    override fun equals(other: Any?): Boolean {
        return true
    }
}