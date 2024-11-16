package ru.sfedu.teamselection.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sfedu.teamselection.domain.Application;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.dto.ApplicationDto;
import ru.sfedu.teamselection.mapper.ApplicationDtoMapper;
import ru.sfedu.teamselection.repository.ApplicationRepository;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.TeamRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final StudentRepository studentRepository;

    private final TeamRepository teamRepository;

    private final ApplicationDtoMapper applicationDtoMapper;


    public Application findByIdOrElseThrow(Long id) throws NoSuchElementException{
        return applicationRepository.findById(id).orElseThrow();
    }

    public List<Application> findAll()
    {
        return applicationRepository.findAll();
    }

    public void delete(Long id)
    {
        applicationRepository.deleteById(id);
    }

    /**
     * Returns a list of teams for which student is subscribed
     * @param id student id
     * @return the new list
     */
    public List<Team> getUserApplications(Long id) {
        Student student = studentRepository.findById(id).orElseThrow();
        if (student.getApplications().isEmpty()) {
            return new ArrayList<>();
        } else {
            return student.getApplications().stream()
                    .map(application ->
                            teamRepository.findById(application.getTeam().getId()).orElseThrow()
                    )
                    .toList();
        }
    }


    public List<Student> findTeamApplications(Long teamId)
    {
        Team team = teamRepository.findById(teamId).orElseThrow();
        if (team.getApplications().isEmpty())
        {
            return new ArrayList<>();
        }
        else {
            return team.getApplications().stream().map(application->studentRepository.findById(application.getStudent().getId()).orElseThrow()).toList();
        }
    }

    public Application create(ApplicationDto dto)
    {
        Application application = applicationDtoMapper.mapToEntity(dto);
        if (applicationRepository.existsByTeamIdAndStudentId(dto.getTeamId(), dto.getStudentId()))
        {
            application = applicationRepository.findByTeamIdAndStudentId(dto.getTeamId(), dto.getStudentId());
            application.setStatus(dto.getStatus());
        }
        return applicationRepository.save(application);
    }

}
