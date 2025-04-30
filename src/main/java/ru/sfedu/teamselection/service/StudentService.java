package ru.sfedu.teamselection.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.student.StudentCreationDto;
import ru.sfedu.teamselection.dto.student.StudentDto;
import ru.sfedu.teamselection.dto.student.StudentSearchOptionsDto;
import ru.sfedu.teamselection.enums.TrackType;
import ru.sfedu.teamselection.exception.ConstraintViolationException;
import ru.sfedu.teamselection.exception.NotFoundException;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.mapper.student.StudentCreationDtoMapper;
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

    private final StudentCreationDtoMapper studentCreationDtoMapper;
    private final TechnologyMapper technologyDtoMapper;

    private final RoleRepository roleRepository;

    @Autowired
    @Lazy
    private TeamService teamService;

    /**
     * Find Student entity by id
     * @param id student id
     * @return entity with given id
     * @throws ru.sfedu.teamselection.exception.NotFoundException in case there is no student with such id
     */
    @Transactional(readOnly = true)
    public Student findByIdOrElseThrow(Long id) throws NotFoundException {
        return studentRepository.findById(id).orElseThrow();
    }

    /**
     * Find all students
     * @return page of students
     */
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Page<Student> search(String like,
                                Long trackId,
                                Integer course,
                                Integer groupNumber,
                                Boolean hasTeam,
                                Boolean isCaptain,
                                List<Long> technologies,
                                Pageable pageable) {

        Specification<Student> specification = Specification.allOf();

        if (like != null) {
            specification = specification.and(StudentSpecification.like(like));
        }

        if (trackId!=null)
        {
            specification = specification.and(StudentSpecification.byTrack(trackId));
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

        Sort sort = pageable.getSort();
        for (Sort.Order order : sort) {
            if ("name".equals(order.getProperty())) {
                Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(order.getDirection(), "user.fio"));
                Page<Student> st = studentRepository.findAll(specification, newPageable);
                return studentRepository.findAll(specification, newPageable);
            }
        }

        return studentRepository.findAll(specification, pageable);
    }


    /**
     * Creates student using given data.
     * @param dto DTO containing student data
     * @return created student
     */
    @Transactional
    public Student create(StudentCreationDto dto) {
        User studentUser = userService.findByIdOrElseThrow(dto.getUserId());
        Role role = roleRepository.findByName("STUDENT").orElseThrow();
        Student student = studentCreationDtoMapper.mapToEntity(dto);

        studentUser.setRole(role);
        student.setUser(studentUser);
        studentRepository.save(student);
        return student;
    }

    /**
     * Deletes student entity
     * @param id student id
     * @throws NotFoundException in case there is no student with such id
     */
    @Transactional
    public void delete(Long id) {
        Student student = findByIdOrElseThrow(id);
        if (student.getHasTeam()) {
            try {
                teamService.removeStudentFromTeam(student.getCurrentTeam(), student);
            } catch (ConstraintViolationException ex) {
                throw new ConstraintViolationException("Can't delete student who is a captain");
            }
        }
        studentRepository.delete(findByIdOrElseThrow(id));
    }




    /**
     * Updates student by id using given data.
     * @param id id of the user.
     * @param dto DTO containing new data.
     * @param isUnsafeAllowed is update limited to safe fields
     * @return updated student
     */
    @Transactional
    public Student update(Long id, StudentDto dto, Boolean isUnsafeAllowed) {
        Student student = findByIdOrElseThrow(id);

        if (isUnsafeAllowed) {
            student.setHasTeam(dto.getHasTeam());
            student.setIsCaptain(dto.getIsCaptain());
        }
        // TODO should user be able to change course?
        // может привести к ошибкам: изменить курс уже после вступления в команду - поломается логика
        student.setCourse(dto.getCourse());
        student.setGroupNumber(dto.getGroupNumber());
        student.setAboutSelf(dto.getAboutSelf());
        student.setContacts(dto.getContacts());
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

    /**
     * Returns DTO containing all possible filter options for searching for students
     * @return new DTO {@link StudentSearchOptionsDto}
     */
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

    /**
     * Returns the student id corresponding to the current (authenticated) user.
     * @return id of the student or null if user is not a student
     */
    @Transactional(readOnly = true)
    public Long getCurrentStudent() {
        User currentUser = userService.getCurrentUser();
        if (studentRepository.existsByUserId(currentUser.getId())) {
            return studentRepository.findByUserId(currentUser.getId()).getId();
        }
        return null;
    }
}
