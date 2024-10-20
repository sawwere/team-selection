package ru.sfedu.teamselection.dto

import ru.sfedu.teamselection.domain.Student
import ru.sfedu.teamselection.domain.Team
import ru.sfedu.teamselection.domain.Users

class StudentDto(
    var id: Long? = null,
    var fio: String? = null,
    var email: String? = null,
    var course: Int? = null,
    var groupNumber: Int? = null,
    var aboutSelf: String? = null,
    var tags: String? = null,
    var contacts: String? = null,
    var status: Boolean? = null,
    var currentTeam: Team? = null,
    var trackId: Long? = null,
    var captain: Boolean? = null,
    var subscriptions: String? = null,
    var user: Users? = null
) {

    companion object{
        fun entityToDto(student: Student): StudentDto {
            return StudentDto().apply {
                id = student.id!!
                fio = student.fio
                email = student.email
                course = student.course
                groupNumber = student.groupNumber
                aboutSelf = student.aboutSelf
                tags = student.tags
                contacts = student.contacts
                status = student.status
                currentTeam =
                    if (student.currentTeam == null) null
                    else Team().apply {
                        id = student.currentTeam!!.id
                        name = student.currentTeam!!.name
                        about = student.currentTeam!!.about
                        projectType = student.currentTeam!!.projectType
                        quantityOfStudents = student.currentTeam!!.quantityOfStudents
                        captainId = student.currentTeam!!.captainId
                        fullFlag = student.currentTeam!!.fullFlag
                        tags = student.currentTeam!!.tags
                        currentTrack = student.currentTeam!!.currentTrack
                        candidates = student.currentTeam!!.candidates
                        students = null
                    }
                user = student.user
                trackId = student.trackId
                captain = student.captain
                subscriptions = student.subscriptions
            }
        }
    }

}