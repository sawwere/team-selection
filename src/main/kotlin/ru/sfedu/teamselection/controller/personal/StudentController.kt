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
        summary = "Поиск студентов по like",
        parameters = [Parameter(name = "input", description = "строка из поиска, разделенная пробелами")]
    )
    @GetMapping("/like")
    fun getLikeStudents(@RequestParam input: String): Any {
        val inputValues = input.split(" ")
        val students = studentRepository.findAll()
        if (students.isEmpty())
            return ResponseEntity<List<StudentDto>>(null, HttpStatus.NOT_FOUND)
        val mappedStudents = students.map { mapper.writeValueAsString(it)  }
        val result = mutableListOf<Student>()
        mappedStudents.forEachIndexed { ind, it ->
            var flag = false
            inputValues.forEach { inp ->
                if (it.contains(inp)) {
                    flag = true
                }
            }
            if (flag) {
                result.add(students[ind])
            }
        }
        val finalResult = mutableListOf<StudentDto>()
        result.forEach {
            finalResult.add(StudentDto.entityToDto(it))
        }
        return ResponseEntity<List<StudentDto>>(finalResult, HttpStatus.OK)
    }

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

    @Operation(
        method = "GET",
        summary = "Найти студентов по их статусу: состоят в команде или нет",
        parameters = [
            Parameter(name = "status", description = "true/false"),
            Parameter(name = "trackId", description = "id текущего трека")
        ]
    )
    @GetMapping("/findByStatusAndTrackId") //checked
    fun findStudentByStatusAndTrackId(@RequestParam status: Boolean, @RequestParam trackId: Long): ResponseEntity<MutableList<StudentDto>> {
        return try {
            val studentList = studentRepository.findStudentByStatusAndTrackId(status, trackId)
            if (studentList.isNotEmpty()) {
                val finalResult = mutableListOf<StudentDto>()
                studentList.forEach {
                    finalResult.add(StudentDto.entityToDto(it))
                }
                ResponseEntity<MutableList<StudentDto>>(finalResult, HttpStatus.OK)
            } else {
                ResponseEntity<MutableList<StudentDto>>(HttpStatus.NOT_FOUND)
            }
        } catch (ex: Exception) {
            log.info{ "Error occurred while writing student to DB: ${ex.message}" }
            ResponseEntity<MutableList<StudentDto>>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(
        method = "GET",
        summary = "Найти студентов по их навыкам(тегам)",
        parameters = [
            Parameter(name = "tags", description = "склеенная строка из тегов через пробелы"),
            Parameter(name = "trackId", description = "id текущего трека")
        ]
    )
    @GetMapping("/findByTagAndTrackId") // checked
    fun findStudentByTag(@RequestParam tags: String, trackId: Long): ResponseEntity<MutableList<StudentDto>> {
        return try {
            val studentList = studentRepository.findStudentByTagsInAndTrackId(tags.split(" "), trackId)
            if (studentList.isNotEmpty()) {
                val finalResult = mutableListOf<StudentDto>()
                studentList.forEach {
                    finalResult.add(StudentDto.entityToDto(it))
                }
                ResponseEntity<MutableList<StudentDto>>(finalResult, HttpStatus.OK)
            } else {
                ResponseEntity<MutableList<StudentDto>>(HttpStatus.NOT_FOUND)
            }
        } catch (ex: Exception) {
            log.info{ "Error occurred while writing student to DB: ${ex.message}" }
            ResponseEntity<MutableList<StudentDto>>(HttpStatus.NOT_FOUND)
        }
    }

}