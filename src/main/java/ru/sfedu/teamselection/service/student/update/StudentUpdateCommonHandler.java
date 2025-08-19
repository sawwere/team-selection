package ru.sfedu.teamselection.service.student.update;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.dto.StudentUpdateDto;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.service.TeamService;

@Component
@RequiredArgsConstructor
public class StudentUpdateCommonHandler implements StudentUpdateHandler {
    protected final TeamService teamService;
    protected final TechnologyMapper technologyDtoMapper;

    @Override
    public void update(Student student, StudentUpdateDto dto) {
        student.setAboutSelf(dto.getAboutSelf());
        student.setContacts(dto.getContacts());
        student.setTechnologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()));

        student.getUser().setIsRemindEnabled(dto.getUser().getIsRemindEnabled());
    }
}
