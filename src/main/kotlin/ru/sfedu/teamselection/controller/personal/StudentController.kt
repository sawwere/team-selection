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
        summary = "Получение данных о студенте по email (необходимо вызывать после авторизации)",
        parameters = [Parameter(name = "email", description = "email без @sfedu.ru")]
    )
    @GetMapping("/{email}") // checked
    fun getUserDataByEmail(@PathVariable email: String): ResponseEntity<StudentDto> {
        val emailString = "$email@sfedu.ru"
        val result = studentRepository.findByEmail(emailString)
        return if (result != null) {
            val dto = StudentDto.entityToDto(student = result)
            ResponseEntity<StudentDto>(dto, HttpStatus.OK)
        }
        else ResponseEntity<StudentDto>(null, HttpStatus.NOT_FOUND)
    }

    @Operation(
        method = "GET",
        summary = "Получение списка всех студентов за все время"
    )
    @GetMapping("/all") // checked
    fun getAllStudents(): ResponseEntity<MutableList<StudentDto>> {
        val result = studentRepository.findAll()
        return if (result.isNotEmpty()){
            val finalResult = mutableListOf<StudentDto>()
            result.forEach {
                finalResult.add(StudentDto.entityToDto(it))
            }
            ResponseEntity<MutableList<StudentDto>>(finalResult, HttpStatus.OK)
        } else{
            ResponseEntity<MutableList<StudentDto>>(null, HttpStatus.METHOD_NOT_ALLOWED)
        }
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
        method = "POST",
        summary = "Регистрация пользователя",
        parameters = [Parameter(name = "student", description = "сущность студента")]
    )
    @PostMapping("/register/{type}") // checked
    fun registerUser(@RequestBody student: Student, @PathVariable type: String) = try {
        val newUser = userRepository.findByEmail(student.email!!)?.apply {
            registered = true
        }
        student.apply {
            user = newUser
        }
        val track = trackRepository.findAllByType(type).filter { it.startDate != null }.maxBy { it.startDate!! }
        student.trackId = track.id
        studentRepository.save(student)
        ResponseEntity<Student>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info{ "Error occurred while writing student to DB: ${ex.message}" }
        ResponseEntity<Student>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "POST",
        summary = "Изменить данные пользователя",
        parameters = [Parameter(name = "student", description = "сущность студента")]
    )
    @PostMapping("/changeStudentData") // checked
    fun changeStudentData(@RequestBody student: Student) = try {
        val foundStudent = studentRepository.findById(student.id!!).getOrNull()
        foundStudent?.apply {
            if (!student.fio.isNullOrEmpty()) {
                fio = student.fio
            }
            if (!student.email.isNullOrEmpty()) {
                email = student.email
            }
            if (student.captain != null) {
                captain = student.captain
            }
            if (student.status != null) {
                status = student.status
            }
            if (student.aboutSelf != null) {
                aboutSelf = student.aboutSelf
            }
            if (student.course != null) {
                course = student.course
            }
            if (student.contacts != null) {
                contacts = student.contacts
            }
            if (student.groupNumber != null) {
                groupNumber = student.groupNumber
            }
            if (student.tags != null) {
                tags = student.tags
            }
        }
        if (foundStudent != null) {
            studentRepository.save(foundStudent)
        }
        ResponseEntity<Student>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info{ "Error occurred while writing student to DB: ${ex.message}" }
        ResponseEntity<Student>(HttpStatus.METHOD_NOT_ALLOWED)
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

    @Operation(
        method = "GET",
        summary = "Найти заявки студентов в различные команды",
        parameters = [
            Parameter(name = "studentId", description = "id студента"),
        ]
    )
    @GetMapping("/getSubscriptionsById") //checked
    fun getStudentSubscriptions(@RequestParam studentId: Long): ResponseEntity<Any> {
        val student = studentRepository.findById(studentId).get()
        return if (student.subscriptions == ""){
            ResponseEntity<Any>(mutableListOf<Team>(), HttpStatus.OK)
        } else {
            val teams = mutableListOf<Team>()
            val ids = student.subscriptions?.split(" ")?.map { it.toLong() }
            ids?.forEach {
                val team = teamRepository.findById(it).getOrNull()
                if (team != null) teams.add(team)
            }
            ResponseEntity(teams, HttpStatus.OK)
        }
    }

    @Operation(
        method = "GET",
        summary = "Получение студента по его id",
        parameters = [
            Parameter(name = "studentId", description = "id студента"),
        ]
    )
    @GetMapping("/getStudentById") // checked
    fun getStudentById(@RequestParam studentId: Long): ResponseEntity<StudentDto> {
        val student = studentRepository.findById(studentId).getOrNull()
        if (student == null) {
            return ResponseEntity<StudentDto>(HttpStatus.NOT_FOUND)
        }
        val result = StudentDto.entityToDto(student)
        return ResponseEntity<StudentDto>(result, HttpStatus.OK)
    }

    @Operation(
        method = "DELETE",
        summary = "Удалить студента по его id",
        parameters = [
            Parameter(name = "studentId", description = "id студента"),
        ]
    )
    @DeleteMapping("/deleteStudentById") // checked
    fun deleteStudentById(@RequestParam studentId: Long) = try {
        studentRepository.deleteById(studentId)
        ResponseEntity<Student>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info{ "Error occurred while writing student to DB: ${ex.message}" }
        ResponseEntity<Student>(HttpStatus.METHOD_NOT_ALLOWED)
    }

}