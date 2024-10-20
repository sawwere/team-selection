package ru.sfedu.teamselection.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.sfedu.teamselection.domain.Team

@Transactional
interface TeamRepository: JpaRepository<Team, Long> {
    fun findAllByFullFlagAndCurrentTrackId(isFull: Boolean, trackId: Long): MutableList<Team>
    fun findTeamByTagsInAndCurrentTrackId(tags: List<String>, trackId: Long): MutableList<Team>
    fun findAllByCurrentTrackId(trackId: Long): MutableList<Team>

}