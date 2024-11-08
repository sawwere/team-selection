package ru.sfedu.teamselection.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.StudentDto;
import ru.sfedu.teamselection.mapper.StudentDtoMapper;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.specification.StudentSpecification;
import ru.sfedu.teamselection.util.TrackByStartComparator;


@RequiredArgsConstructor
@Service
public class StudentService {
    private final StudentRepository studentRepository;

    private final UserService userService;
    private final TeamService teamService;
    private final TrackService trackService;

    private final StudentDtoMapper studentDtoMapper;

    /**
     * Find Student entity by id
     * @param id student id
     * @return entity with given id
     * @throws NoSuchElementException in case there is no student with such id
     */
    public Student findByIdOrElseThrow(Long id) throws NoSuchElementException {
        return studentRepository.findById(id).orElseThrow();
    }

    /**
     * Find all students
     * @return the list of all the students
     */
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student create(StudentDto dto, String type) {
        //TODO
        User newUser = userService.findByEmail("TODO");

        Student student = studentDtoMapper.mapToEntity(dto);

        newUser.setIsEnabled(true);
        student.setUser(newUser);
        Track track = trackService.findAllByType(type)
                .stream()
                .filter(it -> it.getStartDate() != null)
                .max(new TrackByStartComparator())
                .orElseThrow();
        //student.setTrackId(track.getId());
        studentRepository.save(student);
        return student;
    }

    /**
     * Deletes student entity
     * @param id student id
     * @throws NoSuchElementException in case there is no student with such id
     */
    public void delete(Long id) {
        studentRepository.delete(findByIdOrElseThrow(id));
    }

    /**
     * Performs search across all students with given filter criteria
     * @param like like parameter for the student string representation
     * @param email desired student's email
     * @return the filtered list
     */
    public List<Student> search(String like, String email) {
        Specification<Student> specification = Specification.allOf();
        if (like != null) {
            specification = specification.and(StudentSpecification.like(email));
        }
        if (email != null) {
            specification = specification.and(StudentSpecification.byEmail(email));
        }

        return studentRepository.findAll(specification);
    }

    /**
     * Returns a list of teams for which student is subscribed
     * @param id student id
     * @return the new list
     */
    public List<Team> getUserApplications(Long id) {
        Student student = findByIdOrElseThrow(id);
        if (student.getApplications().isEmpty()) {
            return new ArrayList<>();
        } else {
            return student.getApplications().stream()
                    .map(application ->
                            teamService.findByIdOrElseThrow(application.getTeam().getId())
                    )
                    .toList();
        }
    }

    public Student update(Long id, StudentDto dto) {
        Student student = findByIdOrElseThrow(id);

//        student.setFio(dto.getFio());
//        student.setEmail(dto.getEmail());
//        student.setCaptain(dto.getCaptain());
//        student.setStatus(dto.getStatus());
//        student.setAboutSelf(dto.getAboutSelf());
//        student.setCourse(dto.getCourse());
//        student.setContacts(dto.getContacts());
//        student.setGroupNumber(dto.getGroupNumber());
//        student.setTags(dto.getTags());

        studentRepository.save(student);
        return student;
    }
}
