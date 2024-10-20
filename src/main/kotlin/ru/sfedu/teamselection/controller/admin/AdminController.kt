package ru.sfedu.teamselection.controller.admin

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.sfedu.teamselection.enums.Roles
//import ru.sfedu.teamselection.log
import ru.sfedu.teamselection.repository.TrackRepository
import ru.sfedu.teamselection.repository.UserRepository
import ru.sfedu.teamselection.service.ReportService
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "AdminController", description = "API для работы с функциями администратора")
class AdminController(
    private val userRepository: UserRepository,
    private val trackRepository: TrackRepository,
    private val reportService: ReportService
) {

    @Operation(
        method = "GET",
        summary = "Выдача роли по почте пользователя",
        parameters = [
            Parameter(name = "email", description = "email без @sfedu.ru"),
            Parameter(name = "role", description = "Доступные роли: USER, ADMINISTRATOR, SUPER_ADMINISTRATOR")
        ]
    )
    @GetMapping("/giveRole") //checked
    fun giveUserRole(@RequestParam email: String, @RequestParam role: String): ResponseEntity<String> {
        return try{
            userRepository.updateRoleByEmail(email, Roles.valueOf(role))
            ResponseEntity.ok().body("Пользователю $email выдана роль $role")
        } catch (ex: Exception){
            //log.error { "Exception while giving role to user $email: ${ex.message}" }
            ResponseEntity("Не удалось выдать роль пользователю $email", HttpStatus.METHOD_NOT_ALLOWED)
        }
    }

    @Operation(
        method = "GET",
        summary = "Получение отчета по треку с заданным id",
        parameters = [
            Parameter(name = "trackId", description = "id трека"),
        ]
    )
    @GetMapping("/getExcelForTrack", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]) //
    fun createExcelForTrack(@RequestParam trackId: Long): ResponseEntity<ByteArray>? {
        val track = trackRepository.findById(trackId).getOrNull()
        return track?.let { ResponseEntity.ok().body(reportService.trackToExcelFile(track)) } ?: ResponseEntity<ByteArray>(HttpStatus.NOT_FOUND)
    }

}