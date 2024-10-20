package ru.sfedu.teamselection.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import ru.sfedu.teamselection.domain.Users
import ru.sfedu.teamselection.enums.Roles

interface UserRepository: JpaRepository<Users, Long> {

    fun findByEmail(email: String): Users?

    @Modifying
    @Transactional
    @Query("update Users u set u.role = :role where u.email = :email")
    fun updateRoleByEmail(@Param("email") email: String, @Param("role") role: Roles)
}