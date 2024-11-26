package ru.sfedu.teamselection.controller.personal

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.sfedu.teamselection.domain.Student
import ru.sfedu.teamselection.domain.Team
import ru.sfedu.teamselection.dto.StudentDto
import ru.sfedu.teamselection.log
import ru.sfedu.teamselection.repository.StudentRepository
import ru.sfedu.teamselection.repository.TeamRepository
import ru.sfedu.teamselection.repository.TrackRepository
import ru.sfedu.teamselection.repository.UserRepository
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "StudentController", description = "API для работы со студентами")
class StudentController(
    private val studentRepository: StudentRepository,
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository,
    private val trackRepository: TrackRepository
) {

    private val mapper = ObjectMapper()

    @Operation(
        method = "GET",
        summary = "Получение списка всех студентов по текущему треку в зависимости от типа: bachelor/master",
        parameters = [Parameter(name = "type", description = "тип трека bachelor/master")]
    )
    @GetMapping("/allStudentsByCurrentTrack") //TODO
    fun getAllStudentsByCurrentTrack(@RequestParam type: String): ResponseEntity<List<StudentDto>> {
        return try {
            val tracks = trackRepository.findAllByType(type)
            val currentTrack = tracks.maxBy { it.startDate!! }
            val students = studentRepository.findAllByTrackId(currentTrack.id!!)
            val finalResult = mutableListOf<StudentDto>()
            students.forEach {
                finalResult.add(StudentDto.entityToDto(it))
            }
            ResponseEntity<List<StudentDto>>(finalResult, HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(
        method = "GET",
        summary = "Получение списка всех капитанов команд по id трека",
        parameters = [Parameter(name = "trackId", description = "id трека")]
    )
    @GetMapping("/captains") // checked
    fun getAllCaptainsByTrackId(@RequestParam trackId: Long): ResponseEntity<MutableList<StudentDto>> {
        val result = studentRepository.findAllByCaptainAndTrackId(true, trackId)
        val finalResult = mutableListOf<StudentDto>()
        result.forEach {
            finalResult.add(StudentDto.entityToDto(it))
        }
        return if (result.isNotEmpty()){
            ResponseEntity<MutableList<StudentDto>>(finalResult, HttpStatus.OK)
        } else{
            ResponseEntity<MutableList<StudentDto>>(HttpStatus.NOT_FOUND)
        }
    }
}