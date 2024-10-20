package ru.sfedu.teamselection.controller.track

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.sfedu.teamselection.domain.Student
import ru.sfedu.teamselection.domain.Track
import ru.sfedu.teamselection.log
import ru.sfedu.teamselection.repository.TeamRepository
import ru.sfedu.teamselection.repository.TrackRepository
import java.text.SimpleDateFormat
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max

@RestController
@RequestMapping("/api/v1/tracks")
@Tag(name = "TrackController", description = "API для работы с треками")
class TracksController(
    private val trackRepository: TrackRepository,
    private val teamRepository: TeamRepository
) {

    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    @Operation(
        method = "GET",
        summary = "Получение списка всех студентов за все время"
    )
    @GetMapping("/all") // checked
    fun getAllTracks() = trackRepository.findAll().let {
        return@let if (it.isNotEmpty()) ResponseEntity<MutableList<Track>>(it, HttpStatus.OK)
        else ResponseEntity<MutableList<Track>>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "GET",
        summary = "Найти трек по id",
        parameters = [
            Parameter(name = "trackId", description = "id трека")
        ]
    )
    @GetMapping("/trackById") // checked
    fun getTrackById(@RequestParam trackId: Long) = try {
        ResponseEntity.ok(trackRepository.findById(trackId))
    } catch (ex: Exception){
        log.info{ "Error occurred while writing student to DB: ${ex.message}" }
        ResponseEntity<Student>(HttpStatus.NOT_FOUND)
    }

    @Operation(
        method = "POST",
        summary = "Создание трека",
        parameters = [Parameter(name = "track", description = "сущность трека")]
    )
    @PostMapping("/createTrack") // checked
    fun createTrack(@RequestBody track: Track) = try {
        trackRepository.save(track)
        ResponseEntity<Track>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info { "Error occurred while writing student to DB: ${ex.message}" }
        ResponseEntity<Track>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "DELETE",
        summary = "Удалить трек по id",
        parameters = [Parameter(name = "trackId", description = "id трека")]
    )
    @DeleteMapping("/deleteTrack") // checked
    fun deleteTrack(@RequestParam trackId: Long) = try {
        val track = trackRepository.findById(trackId).get()
        track.currentTeams?.forEach {
            val team = teamRepository.findById(it.id!!).get()
            team.currentTrack = null
            teamRepository.save(team)
        }
        trackRepository.deleteById(trackId)
        ResponseEntity<Any>(HttpStatus.OK)
    } catch (ex: Exception) {
        log.info { "Error occurred while writing student to DB: ${ex.message}" }
        ResponseEntity<Any>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "POST",
        summary = "Изменить данные трека",
        parameters = [Parameter(name = "track", description = "сущность трека")]
    )
    @PostMapping("/changeTrack") // checked
    fun changeTrack(@RequestBody track: Track) = try {
        val foundTrack = trackRepository.findById(track.id!!).getOrNull()
        foundTrack?.apply {
            if (!track.type.isNullOrEmpty()) {
                type = track.type
            }
            if (track.startDate != null) {
                startDate = track.startDate
            }
            if (track.endDate != null) {
                endDate = track.endDate
            }
            if (!track.about.isNullOrEmpty()) {
                about = track.about
            }
            if (track.maxConstraint != null) {
                maxConstraint = track.maxConstraint
            }
            if (track.maxThirdCourseConstraint != null) {
                maxThirdCourseConstraint = track.maxThirdCourseConstraint
            }
            if (track.minConstraint != null) {
                minConstraint = track.minConstraint
            }
            if (!track.name.isNullOrEmpty()) {
                name = track.name
            }
        }
        if (foundTrack != null) {
            trackRepository.save(foundTrack)
        }
        ResponseEntity<Track>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info{ "Error occurred while writing student to DB: ${ex.message}" }
        ResponseEntity<Any>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "GET",
        summary = "Найти текущий трек по его типу",
        parameters = [
            Parameter(name = "type", description = "значения bachelor/master")
        ]
    )
    @GetMapping("/currentTrack") // checked
    fun getCurrentTrack(@RequestParam type: String): ResponseEntity<Track> {
        return try {
            val tracks = trackRepository.findAllByType(type).filter { it.startDate != null }
            ResponseEntity<Track>(tracks.maxBy { it.startDate!! }, HttpStatus.OK)
        } catch (ex: Exception) {
            log.info { "Getting current track failed with error: ${ex.message}" }
            ResponseEntity<Track>(HttpStatus.METHOD_NOT_ALLOWED)
        }

    }
}