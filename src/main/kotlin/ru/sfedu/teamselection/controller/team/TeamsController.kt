package ru.sfedu.teamselection.controller.team

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.sfedu.teamselection.domain.Student
import ru.sfedu.teamselection.domain.Team
import ru.sfedu.teamselection.dto.StudentDto
import ru.sfedu.teamselection.log
import ru.sfedu.teamselection.repository.StudentRepository
import ru.sfedu.teamselection.repository.TeamRepository
import ru.sfedu.teamselection.repository.TrackRepository
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/v1/teams")
@Tag(name = "TeamsController", description = "API для работы с командами")
class TeamsController(
    private val teamRepository: TeamRepository,
    private val studentRepository: StudentRepository,
    private val trackRepository: TrackRepository
) {

    private val mapper = ObjectMapper()

    @Operation(
        method = "GET",
        summary = "Поиск команды по id",
        parameters = [Parameter(name = "teamId", description = "id команды")]
    )
    @GetMapping("getTeamByID")
    fun getTeamById(@RequestParam teamId: Long): ResponseEntity<Team> {
        val team = teamRepository.findById(teamId).getOrNull()
        if (team == null) {
            return ResponseEntity<Team>(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity<Team>(team, HttpStatus.OK)
    }

    @Operation(
        method = "GET",
        summary = "Поиск команд по like",
        parameters = [Parameter(name = "input", description = "строка из поиска, разделенная пробелами")]
    )
    @GetMapping("/like")
    fun getLikeTeams(@RequestParam input: String): Any {
        val inputValues = input.split(" ")
        val teams = teamRepository.findAll()
        if (teams.isEmpty())
            return ResponseEntity<List<Team>>(null, HttpStatus.NOT_FOUND)
        val mappedStudents = teams.map { mapper.writeValueAsString(it)  }
        val result = mutableListOf<Team>()
        mappedStudents.forEachIndexed { ind, it ->
            var flag = false
            inputValues.forEach { inp ->
                if (it.contains(inp)) {
                    flag = true
                }
            }
            if (flag) {
                result.add(teams[ind])
            }
        }
        return ResponseEntity<List<Team>>(result, HttpStatus.OK)
    }

    @Operation(
        method = "GET",
        summary = "Получение списка всех команд за все время"
    )
    @GetMapping("/all") // checked
    fun getAllTeams(): ResponseEntity<MutableList<Team>> {
        val result = teamRepository.findAll()
        return if (result.isNotEmpty()){
            ResponseEntity<MutableList<Team>>(result, HttpStatus.OK)
        } else{
            ResponseEntity<MutableList<Team>>(result, HttpStatus.METHOD_NOT_ALLOWED)
        }
    }

    @Operation(
        method = "GET",
        summary = "Получение списка всех команд по текущему треку в зависимости от типа: bachelor/master",
        parameters = [Parameter(name = "type", description = "тип трека bachelor/master")]
    )
    @GetMapping("/allTeamsByCurrentTrack")
    fun getAllTeamsByCurrentTrack(@RequestParam type: String): ResponseEntity<List<Team>> {
        return try {
            val tracks = trackRepository.findAllByType(type)
            val currentTrack = tracks.maxBy { it.startDate!! }
            val teams = teamRepository.findAllByCurrentTrackId(currentTrack.id!!)
            ResponseEntity<List<Team>>(teams, HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(
        method = "POST",
        summary = "Регистрация команды",
        parameters = [
            Parameter(name = "team", description = "сущность команды"),
            Parameter(name = "type", description = "тип трека bachelor/master"),
        ]
    )
    @PostMapping("/createTeam/{type}") // checked
    fun createTeam(@RequestBody team: Team, @PathVariable type: String) = try {
        val track = trackRepository.findAllByType(type).filter { it.startDate != null }.maxBy { it.startDate!! }
        val student = studentRepository.findById(team.captainId!!).get()
        track.currentTeams?.add(team)
        team.currentTrack = track
        team.fullFlag = false
        team.quantityOfStudents = 0
        team.quantityOfStudents = team.quantityOfStudents!! + 1
        team.students = mutableListOf()
        team.students?.add(student)
        student.captain = true
        student.currentTeam = team
        teamRepository.save(team)
        trackRepository.save(track)
        studentRepository.save(student)
        ResponseEntity<Team>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info{ "Error occurred while writing team to DB: ${ex.message}" }
        ResponseEntity<Team>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "POST",
        summary = "Изменить данные команды",
        parameters = [Parameter(name = "team", description = "сущность команды")]
    )
    @PostMapping("/changeTeam") // checked
    fun changeTeam(@RequestBody team: Team) = try {
        val foundTeam = teamRepository.findById(team.id!!).getOrNull()
        foundTeam?.apply {
            if (!team.name.isNullOrEmpty()){
                name = team.name
            }
            if (!team.about.isNullOrEmpty()){
                about = team.about
            }
            if (!team.projectType.isNullOrEmpty()){
                projectType = team.projectType
            }
            if (!team.tags.isNullOrEmpty()){
                tags = team.tags
            }
        }
        if (foundTeam != null){
            teamRepository.save(foundTeam)
        }
        ResponseEntity<Team>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info{ "Error occurred while writing team to DB: ${ex.message}" }
        ResponseEntity<Team>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "GET",
        summary = "Найти команды по их статусу: полностью укомплектованы или нет",
        parameters = [
            Parameter(name = "isFull", description = "true/false"),
            Parameter(name = "trackId", description = "id текущего трека")
        ]
    )
    @GetMapping("/findByStatusAndTrackId") // checked
    fun findTeamByStatusAndTrackId(@RequestParam isFull: Boolean, @RequestParam trackId: Long): ResponseEntity<MutableList<Team>> {
        return try {
            val teams = teamRepository.findAllByFullFlagAndCurrentTrackId(isFull, trackId)
            if (teams.isNotEmpty()) {
                ResponseEntity<MutableList<Team>>(teams, HttpStatus.OK)
            } else {
                ResponseEntity<MutableList<Team>>(HttpStatus.NOT_FOUND)
            }
        } catch (ex: Exception) {
            log.info{ "Error occurred while writing team to DB: ${ex.message}" }
            ResponseEntity<MutableList<Team>>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(
        method = "GET",
        summary = "Найти команды по их требованиям(тегам)",
        parameters = [
            Parameter(name = "tags", description = "склеенная строка из тегов через пробелы"),
            Parameter(name = "trackId", description = "id текущего трека")
        ]
    )
    @GetMapping("/findByTagsAndTrackId") //checked
    fun findTeamByTagsAndTrackId(@RequestParam tags: String, @RequestParam trackId: Long): ResponseEntity<MutableList<Team>> {
        return try {
            val teams = teamRepository.findTeamByTagsInAndCurrentTrackId(tags.split(" "), trackId)
            if (teams.isNotEmpty()) {
                ResponseEntity<MutableList<Team>>(teams, HttpStatus.OK)
            } else {
                ResponseEntity<MutableList<Team>>(HttpStatus.NOT_FOUND)
            }
        } catch (ex: Exception) {
            ResponseEntity<MutableList<Team>>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(
        method = "GET",
        summary = "Закинуть заявку в команду",
        parameters = [
            Parameter(name = "studentId", description = "id студента"),
            Parameter(name = "teamId", description = "id команды"),
        ]
    )
    @GetMapping("/subscribe") // checked
    fun subscribeForTeam(@RequestParam studentId: Long, @RequestParam teamId: Long) = try {
        val team = teamRepository.findById(teamId).get()
        val student = studentRepository.findById(studentId).get()
        student.subscriptions += "$teamId "
        if (team.candidates == null)
            team.candidates = ""
        team.candidates += "$studentId "
        teamRepository.save(team)
        studentRepository.save(student)
        ResponseEntity<MutableList<Team>>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info { "Error occurred while writing team to DB: ${ex.message}" }
        ResponseEntity<MutableList<Team>>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "GET",
        summary = "Принять студента в команду",
        parameters = [
            Parameter(name = "studentId", description = "id студента"),
            Parameter(name = "teamId", description = "id команды"),
        ]
    )
    @GetMapping("/approveStudent") // checked
    fun approveStudent(@RequestParam studentId: Long, @RequestParam teamId: Long) = try {
        val team = teamRepository.findById(teamId).get()
        val student = studentRepository.findById(studentId).get()
        if (student.status == true || team.fullFlag == true){
            log.info { "Student is already in another team or team is full" }
            ResponseEntity<Any>("Студент не может быть добавлен: он состоит в другой команде или в вашей команде нет места", HttpStatus.METHOD_NOT_ALLOWED)
        } else {
            student.status = true
            student.currentTeam = team
            student.subscriptions = ""
            team.quantityOfStudents = team.quantityOfStudents?.plus(1)
            team.candidates = team.candidates?.replace("$studentId ", "")
            if (team.quantityOfStudents?.plus(1)  == team.currentTrack!!.maxConstraint)
                team.fullFlag = true
            studentRepository.save(student)
            teamRepository.save(team)
            ResponseEntity<Any>(HttpStatus.OK)
        }
    } catch (ex: Exception){
        log.info { "Error occurred while writing team to DB: ${ex.message}" }
        ResponseEntity<Any>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "GET",
        summary = "Отклонить заявку студента в команду",
        parameters = [
            Parameter(name = "studentId", description = "id студента"),
            Parameter(name = "teamId", description = "id команды"),
        ]
    )
    @GetMapping("/declineStudent") //checked
    fun declineStudent(@RequestParam studentId: Long, @RequestParam teamId: Long) = try {
        val team = teamRepository.findById(teamId).get()
        team.students?.removeIf { it.id == studentId }
        val student = studentRepository.findById(studentId).getOrNull()
        if (student !== null){
            student.subscriptions = (student.subscriptions + " ").replace("$teamId ", "")
            team.candidates = (team.candidates + " ").replace("$studentId ", "")
        }
        teamRepository.save(team)
        ResponseEntity<Any>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info { "Error occurred while writing team to DB: ${ex.message}" }
        ResponseEntity<Any>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "DELETE",
        summary = "Удалить студента из команды",
        parameters = [
            Parameter(name = "studentId", description = "id студента"),
            Parameter(name = "teamId", description = "id команды"),
        ]
    )
    @DeleteMapping("/deleteStudent") // checked
    fun deleteStudent(@RequestParam studentId: Long, @RequestParam teamId: Long) = try {
        val team = teamRepository.findById(teamId).get()
        val student = studentRepository.findById(studentId).get()
        student.status = false
        student.currentTeam = null
        team.students?.removeIf { it.id == studentId }
        team.quantityOfStudents = team.quantityOfStudents?.minus(1)
        if (team.quantityOfStudents!! < team.currentTrack!!.maxConstraint!!)
            team.fullFlag = false
        teamRepository.save(team)
        studentRepository.save(student)
        ResponseEntity<Any>(HttpStatus.OK)
    } catch (ex: Exception){
        log.info { "Error occurred while writing team to DB: ${ex.message}" }
        ResponseEntity<Any>(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Operation(
        method = "GET",
        summary = "Получение кандидатов команды по id",
        parameters = [
            Parameter(name = "teamId", description = "id команды"),
        ]
    )
    @GetMapping("/candidates") // checked
    fun getAllCandidates(@RequestParam teamId: Long): ResponseEntity<MutableList<Student>> {
        val team = teamRepository.findById(teamId).get()
        if (team.candidates.isNullOrEmpty()){
            return ResponseEntity(mutableListOf(), HttpStatus.OK)
        }
        val candidates = studentRepository.findAllByIdIn(team.candidates?.trim()?.split(" ")?.map { it.toLong() }!!)
        return ResponseEntity(candidates, HttpStatus.OK)
    }

    @Operation(
        method = "DELETE",
        summary = "Удалить команду по id",
        parameters = [
            Parameter(name = "teamId", description = "id команды"),
        ]
    )
    @DeleteMapping("/deleteTeamById") //checked
    fun deleteTeamById(@RequestParam teamId: Long) = try {
        val team = teamRepository.findById(teamId).get()
        team.students?.forEach {
            val student = studentRepository.findById(it.id!!).get()
            student.currentTeam = null
            student.captain = false
            studentRepository.save(student)
        }
        teamRepository.deleteById(teamId)
        ResponseEntity<Team>(HttpStatus.OK)
    } catch (ex: Exception) {
        log.info { "Error occurred while writing team to DB: ${ex.message}" }
        ResponseEntity<MutableList<Team>>(HttpStatus.METHOD_NOT_ALLOWED)
    }

}