package ru.sfedu.teamselection.domain

import ru.sfedu.teamselection.enums.Roles
import jakarta.persistence.*


@Entity
@Table(name = "users")
class Users(
    @Column
    var fio: String? = null,
    @Column
    var email: String? = null,
    @Column
    @Enumerated(EnumType.STRING)
    var role: Roles? = null,
    @Column
    var registered: Boolean? = null,
): Domain()