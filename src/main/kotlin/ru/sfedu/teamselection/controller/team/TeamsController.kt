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