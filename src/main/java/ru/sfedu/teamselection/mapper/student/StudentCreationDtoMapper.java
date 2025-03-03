package ru.sfedu.teamselection.mapper.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.dto.student.StudentCreationDto;
import ru.sfedu.teamselection.mapper.DtoMapper;
import ru.sfedu.teamselection.mapper.user.UserMapper;

@Component
public class StudentCreationDtoMapper implements DtoMapper<StudentCreationDto, Student> {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Student mapToEntity(StudentCreationDto dto) {
        return Student.builder()
                .id(null)
                .course(dto.getCourse())
                .groupNumber(dto.getGroupNumber())
                .aboutSelf(dto.getAboutSelf())
                .contacts(dto.getContacts())
                .currentTeam(null)
                .user(null)
                .build();
    }

    @Override
    public StudentCreationDto mapToDto(Student entity) {
        return StudentCreationDto.builder()
                .course(entity.getCourse())
                .groupNumber(entity.getGroupNumber())
                .aboutSelf(entity.getAboutSelf())
                .contacts(entity.getContacts())
                .userId(entity.getUser().getId())
                .build();
    }
}
