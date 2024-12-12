package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.dto.StudentCreationDto;
import ru.sfedu.teamselection.dto.StudentDto;

@Component
@RequiredArgsConstructor
public class StudentDtoMapper implements DtoMapper<StudentDto, Student> {
    private final TechnologyDtoMapper technologyDtoMapper;
    private final ApplicationCreationDtoMapper applicationCreationDtoMapper;
    @Autowired
    private UserDtoMapper userDtoMapper;
    @Lazy
    @Autowired
    private TeamDtoMapper teamDtoMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Student mapToEntity(StudentDto dto) {
        return Student.builder()
                .id(dto.getId())
                .course(dto.getCourse())
                .groupNumber(dto.getGroupNumber())
                .aboutSelf(dto.getAboutSelf())
                .contacts(dto.getContacts())
                .hasTeam(dto.getHasTeam())
                .isCaptain(dto.getIsCaptain())
                .currentTeam(teamDtoMapper.mapToEntity(dto.getCurrentTeam()))
                //.user(dto.getUser())
                .technologies(dto.getTechnologies().stream().map(technologyDtoMapper::mapToEntity).toList())
                .applications(dto.getApplications().stream().map(applicationCreationDtoMapper::mapToEntity).toList())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentDto mapToDto(Student entity) {
        return StudentDto.builder()
                .id(entity.getId())
                .course(entity.getCourse())
                .groupNumber(entity.getGroupNumber())
                .aboutSelf(entity.getAboutSelf())
                .contacts(entity.getContacts())
                .hasTeam(entity.getHasTeam())
                .isCaptain(entity.getIsCaptain())
                .currentTeam(teamDtoMapper.mapToDtoWithoutStudents(entity.getCurrentTeam()))
                .user(userDtoMapper.mapToDto(entity.getUser()))
                .technologies(entity.getTechnologies().stream().map(technologyDtoMapper::mapToDto).toList())
                .applications(entity.getApplications().stream().map(applicationCreationDtoMapper::mapToDto).toList())
                .build();
    }


    public StudentDto mapToDtoWithoutTeam(Student entity) {
        return StudentDto.builder()
                .id(entity.getId())
                .course(entity.getCourse())
                .groupNumber(entity.getGroupNumber())
                .aboutSelf(entity.getAboutSelf())
                .contacts(entity.getContacts())
                .hasTeam(entity.getHasTeam())
                .isCaptain(entity.getIsCaptain())
                .user(userDtoMapper.mapToDto(entity.getUser()))
                .technologies(entity.getTechnologies().stream().map(technologyDtoMapper::mapToDto).toList())
                .applications(entity.getApplications().stream().map(applicationCreationDtoMapper::mapToDto).toList())
                .build();
    }

    public Student mapCreationToEntity(StudentCreationDto dto) {
        return Student.builder()
                .course(dto.getCourse())
                .groupNumber(dto.getGroupNumber())
                .aboutSelf(dto.getAboutSelf())
                .contacts(dto.getContacts())
                .currentTeam(null)
                .user(null)
                .build();
    }
}
