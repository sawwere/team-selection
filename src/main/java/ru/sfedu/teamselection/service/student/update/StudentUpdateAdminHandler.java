package ru.sfedu.teamselection.service.student.update;

import java.util.Objects;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.StudentUpdateDto;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.service.TeamService;

@Component
public class StudentUpdateAdminHandler extends StudentUpdateCommonHandler {

    public StudentUpdateAdminHandler(TeamService teamService, TechnologyMapper technologyDtoMapper) {
        super(teamService, technologyDtoMapper);
    }

    @Override
    public void update(Student student, StudentUpdateDto dto) {
        super.update(student, dto);

        student.setHasTeam(dto.getHasTeam());
        student.setIsCaptain(dto.getIsCaptain());

        updateTrack(student, dto);
        updateTeam(student, dto);

        student.getUser().setIsEnabled(dto.getUser().getIsEnabled());
        student.getUser().setEmail(dto.getUser().getEmail());
    }

    private void updateTeam(Student student, StudentUpdateDto dto) {
        Long oldTeamId = student.getCurrentTeam() != null
                ? student.getCurrentTeam().getId()
                : null;
        Long newTeamId = dto.getCurrentTeam() != null
                ? dto.getCurrentTeam().getId()
                : null;

        // если команда изменилась — сначала удалить из старой, потом добавить в новую
        if (!Objects.equals(oldTeamId, newTeamId)) {
            // удаляем из старой
            if (oldTeamId != null) {
                Team oldTeam = teamService.findByIdOrElseThrow(oldTeamId);
                teamService.removeStudentFromTeam(oldTeam, student);
            }
            // добавляем в новую
            if (newTeamId != null) {
                Team newTeam = teamService.findByIdOrElseThrow(newTeamId);
                teamService.addStudentToTeam(newTeam, student, false);
            }
        }
    }

    private void updateTrack(Student student, StudentUpdateDto dto) {
        Long oldTrackId = student.getCurrentTrack() != null
                ? student.getCurrentTrack().getId()
                : null;
        Long newTrackId = dto.getCurrentTrack() != null
                ? dto.getCurrentTrack().getId()
                : null;

//        if (!Objects.equals(oldTrackId, newTrackId)) {
//            if (newTrackId != null) {
//                Track newTrack = trackService.findByIdOrElseThrow(newTrackId);
//                existing.getStudent().setCurrentTrack(newTrack);
//            }
//        }
    }
}
