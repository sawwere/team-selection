package ru.sfedu.teamselection.mapper.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.dto.StudentCreationDto;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.UserDtoMapper;
import ru.sfedu.teamselection.mapper.team.TeamDtoMapper;

@Component
public class StudentCreationDtoMapper implements DtoMapper<StudentCreationDto, Student> {
    @Autowired
    private UserDtoMapper userDtoMapper;
    @Lazy
    @Autowired
    private TeamDtoMapper teamDtoMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Student mapToEntity(StudentCreationDto dto) {
        return Student.builder()
                .id(dto.getId())
                .course(dto.getCourse())
                .groupNumber(dto.getGroupNumber())
                .aboutSelf(dto.getAboutSelf())
                .contacts(dto.getContacts())
                .currentTeam(null)
                .user(null)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentCreationDto mapToDto(Student entity) {
        return StudentCreationDto.builder()
                .id(entity.getId())
                .fio(entity.getUser().getFio())
                .course(entity.getCourse())
                .groupNumber(entity.getGroupNumber())
                .aboutSelf(entity.getAboutSelf())
                .contacts(entity.getContacts())
                .userId(entity.getUser().getId())
                .build();
    }
}
