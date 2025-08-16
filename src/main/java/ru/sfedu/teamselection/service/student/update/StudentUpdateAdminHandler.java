package ru.sfedu.teamselection.service.student.update;

import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
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

        student.getUser().setIsEnabled(dto.getUser().getIsEnabled());
        student.getUser().setEmail(dto.getUser().getEmail());
    }
}
