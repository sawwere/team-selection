package ru.sfedu.teamselection.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.sfedu.teamselection.domain.Student

@Transactional
interface StudentRepository: JpaRepository<Student, Long> {

    fun findByEmail(email: String): Student?
    fun findAllByCaptainAndTrackId(flag: Boolean, id: Long): MutableList<Student>
    fun findStudentByStatusAndTrackId(flag: Boolean, trackId: Long): MutableList<Student>
    fun findStudentByTagsInAndTrackId(tags: List<String>, trackId: Long): MutableList<Student>
    fun findAllByIdIn(ids: List<Long>): MutableList<Student>
    fun findAllByTrackId(id: Long): List<Student>
}