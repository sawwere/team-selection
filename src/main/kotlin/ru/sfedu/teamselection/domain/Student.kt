package ru.sfedu.teamselection.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.context.properties.bind.DefaultValue
import ru.sfedu.teamselection.enums.Roles
import jakarta.persistence.*

@Entity
@Table(name = "students")
@Access(AccessType.FIELD)
class Student(
    @Column
    @JsonProperty
    var fio: String? = null,
    @Column
    @JsonProperty
    var email: String? = null,
    @Column
    @JsonProperty
    var course: Int? = null,
    @Column
    @JsonProperty
    var groupNumber: Int? = null,
    @Column
    @JsonProperty
    var aboutSelf: String? = null,
    @Column
    @JsonProperty
    var tags: String? = null, // Строка разделенная пробелами
    @Column
    @JsonProperty
    var contacts: String? = null,
    @Column
    @JsonProperty
    @DefaultValue(value = ["false"])
    var status: Boolean? = null, // статус участик команды/не участник
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    var currentTeam: Team? = null,
    @Column
    var trackId: Long? = null,
    @Column
    @DefaultValue(value = ["false"])
    @JsonProperty
    var captain: Boolean? = null, //TODO поменять логику начального значения
    @Column
    @JsonProperty
    var subscriptions: String? = "",
    @OneToOne
    @JsonProperty
    var user: Users? = null
): Domain() {
    override fun toString(): String { //TODO fix
        return ""
    }

    override fun equals(other: Any?): Boolean {
        return true
    }
}