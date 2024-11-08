package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.StudentDto;

@Component
@RequiredArgsConstructor
public class StudentDtoMapper implements DtoMapper<StudentDto, Student> {
    private final TechnologyDtoMapper technologyDtoMapper;
    private final ApplicationDtoMapper applicationDtoMapper;
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
                .currentTeam(null)
                .user(dto.getUser())
                .technologies(dto.getTechnologies().stream().map(technologyDtoMapper::mapToEntity).toList())
                .applications(dto.getApplications().stream().map(applicationDtoMapper::mapToEntity).toList())
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
                .currentTeam(mapTeam(entity))
                .user(entity.getUser())
                .technologies(entity.getTechnologies().stream().map(technologyDtoMapper::mapToDto).toList())
                .applications(entity.getApplications().stream().map(applicationDtoMapper::mapToDto).toList())
                .build();
    }

    private Team mapTeam(Student student) {
        if (student.getCurrentTeam() == null) {
            return null;
        }
        return Team.builder()
                .id(student.getCurrentTeam().getId())
                .name(student.getCurrentTeam().getName())
 //               .about(student.getCurrentTeam().getAbout())
                .projectType(student.getCurrentTeam().getProjectType())
                .quantityOfStudents(student.getCurrentTeam().getQuantityOfStudents())
                .captainId(student.getCurrentTeam().getCaptainId())
                .isFull(student.getCurrentTeam().getIsFull())
                //.tags(student.getCurrentTeam().getTags())
                .currentTrack(student.getCurrentTeam().getCurrentTrack())
//                .candidates(student.getCurrentTeam().getCandidates())
                .students(null)
                .build();
    }
}
