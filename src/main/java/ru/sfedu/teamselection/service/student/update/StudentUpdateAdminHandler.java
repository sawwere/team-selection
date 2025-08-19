package ru.sfedu.teamselection.service.student.update;

import java.util.Objects;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.dto.StudentUpdateDto;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.service.TeamService;
import ru.sfedu.teamselection.service.TrackService;

@Component
public class StudentUpdateAdminHandler extends StudentUpdateCommonHandler {
    private final TrackService trackService;

    public StudentUpdateAdminHandler(
            TeamService teamService,
            TechnologyMapper technologyDtoMapper,
            TrackService trackService
    ) {
        super(teamService, technologyDtoMapper);
        this.trackService = trackService;
    }

    @Override
    public void update(Student student, StudentUpdateDto dto) {
        super.update(student, dto);

        student.setCourse(dto.getCourse());
        student.setGroupNumber(dto.getGroupNumber());

        updateTrack(student, dto);
        updateTeam(student, dto);

        student.getUser().setFio(dto.getUser().getFio());
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

        if (!Objects.equals(oldTrackId, newTrackId)) {
            if (newTrackId != null) {
                Track newTrack = trackService.findByIdOrElseThrow(newTrackId);
                student.setCurrentTrack(newTrack);
            }
        }
    }
}
