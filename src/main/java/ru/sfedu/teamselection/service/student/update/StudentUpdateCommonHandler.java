package ru.sfedu.teamselection.service.student.update;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.StudentUpdateDto;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.service.TeamService;

@Component
@RequiredArgsConstructor
public class StudentUpdateCommonHandler implements StudentUpdateHandler {
    private final TeamService teamService;
    private final TechnologyMapper technologyDtoMapper;

    @Override
    public void update(Student student, StudentUpdateDto dto) {
        student.setCourse(dto.getCourse());
        student.setGroupNumber(dto.getGroupNumber());
        student.setAboutSelf(dto.getAboutSelf());
        student.setContacts(dto.getContacts());
        student.setTechnologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()));
        var newTeamDto = dto.getCurrentTeam();
        if (newTeamDto != null && newTeamDto.getId() != null) {
            Team newTeam = teamService.findByIdOrElseThrow(newTeamDto.getId());

            student.setCurrentTeam(newTeam);

            if (student.getTeams().stream().noneMatch(t -> t.getId().equals(newTeamDto.getId()))) {
                student.getTeams().add(newTeam);
            }
        } else {
            student.setCurrentTeam(null);
        }

        student.getUser().setFio(dto.getUser().getFio());
        student.getUser().setIsRemindEnabled(dto.getUser().getIsRemindEnabled());
    }
}
