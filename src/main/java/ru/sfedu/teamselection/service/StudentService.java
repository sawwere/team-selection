package ru.sfedu.teamselection.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.StudentCreationDto;
import ru.sfedu.teamselection.dto.StudentDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.mapper.StudentDtoMapper;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.specification.StudentSpecification;


@RequiredArgsConstructor
@Service
public class StudentService {
    private final StudentRepository studentRepository;

    private final UserService userService;

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

    public Student create(StudentCreationDto dto) {
        User newUser = userService.findByIdOrElseThrow(dto.getUserId());

        Student student = studentDtoMapper.mapCreationToEntity(dto);

        newUser.setIsEnabled(true);
        student.setUser(newUser);
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
     * @param course student's group
     * @param groupNumber student's course
     * @param hasTeam student's status of having team
     * @param technologies student's technologies(skills)
     * @return the filtered list
     */
    public List<Student> search(String like,
                                Integer course,
                                Integer groupNumber,
                                Boolean hasTeam,
                                List<String> technologies) {
        Specification<Student> specification = Specification.allOf();
        if (like != null) {
            specification = specification.and(StudentSpecification.like(like));
        }
        if (course != null) {
            specification = specification.and(StudentSpecification.byCourse(course));
        }
        if (groupNumber != null) {
            specification = specification.and(StudentSpecification.byGroup(groupNumber));
        }
        if (hasTeam != null) {
            specification = specification.and(StudentSpecification.byHasTeam(hasTeam));
        }

        List<Student> findResult = studentRepository.findAll(specification);
        if (technologies != null && !technologies.isEmpty()) {
            List<Student> result = new ArrayList<>();
            for (Student student : findResult) {
                for (var tech :student.getTechnologies()) {
                    if (technologies.contains(tech.getName())) {
                        result.add(student);
                    }
                }
            }
            return result;
        }

        return findResult;
    }



    public Student update(Long id, StudentDto dto) {
        Student student = findByIdOrElseThrow(id);
        student.setCourse(dto.getCourse());
        student.setGroupNumber(dto.getGroupNumber());
        student.setAboutSelf(dto.getAboutSelf());
        student.setContacts(dto.getContacts());
        student.setHasTeam(dto.getHasTeam());
        student.setIsCaptain(dto.getIsCaptain());
        // TODO technologies, applications, team ??

        studentRepository.save(student);
        return student;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public TrackType typeOfStudentTrack(Student student) {
        return switch (student.getCourse()) {
            case 1, 2 -> TrackType.bachelor;
            case 5 -> TrackType.master;
            default -> null;
        };
    }
}
