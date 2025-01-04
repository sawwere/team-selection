package ru.sfedu.teamselection.service;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.StudentCreationDto;
import ru.sfedu.teamselection.dto.StudentDto;
import ru.sfedu.teamselection.dto.StudentSearchOptionsDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.mapper.TechnologyDtoMapper;
import ru.sfedu.teamselection.mapper.student.StudentCreationDtoMapper;
import ru.sfedu.teamselection.mapper.student.StudentDtoMapper;
import ru.sfedu.teamselection.repository.RoleRepository;
import ru.sfedu.teamselection.repository.StudentRepository;
import ru.sfedu.teamselection.repository.TechnologyRepository;
import ru.sfedu.teamselection.repository.specification.StudentSpecification;


@RequiredArgsConstructor
@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final TechnologyRepository technologyRepository;

    private final UserService userService;

    private final StudentDtoMapper studentDtoMapper;
    private final StudentCreationDtoMapper studentCreationDtoMapper;
    private final TechnologyDtoMapper technologyDtoMapper;

    private final RoleRepository roleRepository;

    /**
     * Find Student entity by id
     * @param id student id
     * @return entity with given id
     * @throws NoSuchElementException in case there is no student with such id
     */
    @Transactional(readOnly = true)
    public Student findByIdOrElseThrow(Long id) throws NoSuchElementException {
        return studentRepository.findById(id).orElseThrow();
    }

    /**
     * Find all students
     * @return the list of all the students
     */
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional
    public Student create(StudentCreationDto dto) {
        User newUser = userService.findByIdOrElseThrow(dto.getUserId());
        Role role = roleRepository.findByName("STUDENT").orElseThrow();
        Student student = studentCreationDtoMapper.mapToEntity(dto);

        newUser.setIsEnabled(true);
        newUser.setRole(role);
        student.setUser(newUser);
        studentRepository.save(student);
        return student;
    }

    /**
     * Deletes student entity
     * @param id student id
     * @throws NoSuchElementException in case there is no student with such id
     */
    @Transactional
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
    @Transactional(readOnly = true)
    public List<Student> search(String like,
                                Integer course,
                                Integer groupNumber,
                                Boolean hasTeam,
                                Boolean isCaptain,
                                List<Long> technologies) {
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
        if (isCaptain != null) {
            specification = specification.and(StudentSpecification.byIsCaptain(isCaptain));
        }
        specification = specification.and(StudentSpecification.hasTechnologies(technologies));

        return studentRepository.findAll(specification);
    }


    @Transactional
    public Student update(Long id, StudentDto dto) {
        Student student = findByIdOrElseThrow(id);
        student.setCourse(dto.getCourse());
        student.setGroupNumber(dto.getGroupNumber());
        student.setAboutSelf(dto.getAboutSelf());
        student.setContacts(dto.getContacts());
        student.setHasTeam(dto.getHasTeam());
        student.setIsCaptain(dto.getIsCaptain());
        student.setTechnologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()));
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

    @Transactional(readOnly = true)
    public StudentSearchOptionsDto getSearchOptionsStudents() {
        var students = findAll();

        StudentSearchOptionsDto studentSearchOptionsDto = new StudentSearchOptionsDto();
        for (Student student : students) {
            studentSearchOptionsDto.getCourses().add(student.getCourse());
            studentSearchOptionsDto.getGroups().add(student.getGroupNumber());
        }
        studentSearchOptionsDto.getTechnologies().addAll(technologyRepository.findAll()
                .stream()
                .map(technologyDtoMapper::mapToDto)
                .toList()
        );
        return studentSearchOptionsDto;
    }

    @Transactional(readOnly = true)
    public Long getCurrentStudent()
    {
        User currentUser = userService.getCurrentUser();
        if (studentRepository.existsByUserId(currentUser.getId()))
        {
            return studentRepository.findByUserId(currentUser.getId()).getId();
        }
        return null;
    }
}
