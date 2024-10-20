package ru.sfedu.teamselection.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.sfedu.teamselection.domain.Track

@Transactional
interface TrackRepository: JpaRepository<Track, Long>{
    fun findAllByType(type: String): List<Track>
}