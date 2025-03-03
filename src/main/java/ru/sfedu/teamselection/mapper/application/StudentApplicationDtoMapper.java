package ru.sfedu.teamselection.mapper.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.dto.application.StudentApplicationDto;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.user.UserMapper;

@Component
public class StudentApplicationDtoMapper implements DtoMapper<StudentApplicationDto, Student> {
    @Autowired
    private UserMapper userMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Student mapToEntity(StudentApplicationDto dto) {
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
    public StudentApplicationDto mapToDto(Student entity) {
        return StudentApplicationDto.builder()
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
