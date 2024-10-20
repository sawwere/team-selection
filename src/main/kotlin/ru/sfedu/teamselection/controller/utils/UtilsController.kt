package ru.sfedu.teamselection.controller.utils

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.sfedu.teamselection.enums.SkillTags

@RestController
@Tag(name = "UtilsController", description = "Служебное API")
class UtilsController {
    @Operation(
        method = "GET",
        summary = "Получение списка всех тегов",
    )
    @GetMapping("/api/v1/tags") // checked
    fun returnTags(): ResponseEntity<String> {
        val res = SkillTags.entries.joinToString(" ") { it.name }
        return ResponseEntity<String>(res, HttpStatus.OK)
    }
}