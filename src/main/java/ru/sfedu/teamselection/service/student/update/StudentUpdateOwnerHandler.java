package ru.sfedu.teamselection.service.student.update;

import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.dto.StudentUpdateDto;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.service.TeamService;

@Component
public class StudentUpdateOwnerHandler extends StudentUpdateCommonHandler {

    public StudentUpdateOwnerHandler(TeamService teamService, TechnologyMapper technologyDtoMapper) {
        super(teamService, technologyDtoMapper);
    }

    @Override
    public void update(Student student, StudentUpdateDto dto) {
        super.update(student, dto);
    }
}
