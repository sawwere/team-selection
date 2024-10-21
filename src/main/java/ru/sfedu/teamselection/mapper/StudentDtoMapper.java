package ru.sfedu.teamselection.mapper;

import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.StudentDto;

@Component
public class StudentDtoMapper implements DtoMapper<StudentDto, Student> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Student mapToEntity(StudentDto dto) {
        return Student.builder()
                .id(dto.getId())
                .fio(dto.getFio())
                .email(dto.getEmail())
                .course(dto.getCourse())
                .aboutSelf(dto.getAboutSelf())
                .tags(dto.getTags())
                .contacts(dto.getContacts())
                .status(dto.getStatus())
                .currentTeam(null)
                .user(dto.getUser())
                .trackId(dto.getTrackId())
                .captain(dto.getCaptain())
                .subscriptions(dto.getSubscriptions())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentDto mapToDto(Student entity) {
        return StudentDto.builder()
                .id(entity.getId())
                .fio(entity.getFio())
                .email(entity.getEmail())
                .course(entity.getCourse())
                .aboutSelf(entity.getAboutSelf())
                .tags(entity.getTags())
                .contacts(entity.getContacts())
                .status(entity.getStatus())
                .currentTeam(mapTeam(entity))
                .user(entity.getUser())
                .trackId(entity.getTrackId())
                .captain(entity.getCaptain())
                .subscriptions(entity.getSubscriptions())
                .build();
    }

    private Team mapTeam(Student student) {
        if (student.getCurrentTeam() == null) {
            return null;
        }
        return Team.builder()
                .id(student.getCurrentTeam().getId())
                .name(student.getCurrentTeam().getName())
                .about(student.getCurrentTeam().getAbout())
                .projectType(student.getCurrentTeam().getProjectType())
                .quantityOfStudents(student.getCurrentTeam().getQuantityOfStudents())
                .captainId(student.getCurrentTeam().getCaptainId())
                .isFull(student.getCurrentTeam().getIsFull())
                .tags(student.getCurrentTeam().getTags())
                .currentTrack(student.getCurrentTeam().getCurrentTrack())
                .candidates(student.getCurrentTeam().getCandidates())
                .students(null)
                .build();
    }
}
