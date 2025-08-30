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
        if (dto.getAboutSelf() != null) {
            student.setAboutSelf(dto.getAboutSelf());
        }
        if (dto.getContacts() != null) {
            student.setContacts(dto.getContacts());
        }
        if (dto.getTechnologies() != null) {
            student.setTechnologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()));
        }
        student.getUser().setIsRemindEnabled(dto.getUser().getIsRemindEnabled());
    }
}
