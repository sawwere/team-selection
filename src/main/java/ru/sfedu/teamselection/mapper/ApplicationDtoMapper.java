package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.ApplicationDto;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.TeamRepository;

@Component
@RequiredArgsConstructor
public class ApplicationDtoMapper implements DtoMapper<ApplicationDto, Application>{

    private final TeamRepository teamRepository;

    private final StudentRepository studentRepository;

    private final TeamDtoMapper teamDtoMapper;

    @Override
    public Application mapToEntity(ApplicationDto dto) {
        return Application.builder()
                .id(dto.getId())
                .team()
                .status(dto.getStatus())
                .student(studentRepository.findById(dto.getStudent().))
                .build();;
    }

    @Override
    public ApplicationDto mapToDto(Application entity) {
        return null;
    }
}
