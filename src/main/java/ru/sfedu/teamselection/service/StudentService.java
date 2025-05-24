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
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.student.StudentCreationDto;
import ru.sfedu.teamselection.dto.student.StudentDto;
import ru.sfedu.teamselection.dto.student.StudentSearchOptionsDto;
import ru.sfedu.teamselection.enums.TrackType;
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

    @Lazy
    @Autowired
    private UserService userService;

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
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Студент не найден, id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Student> findAllByTrack(Long trackId, Sort sort) {
        Specification<Student> spec = StudentSpecification.byTrack(trackId);
        return studentRepository.findAll(spec, sort);
    }

    /**
     * Find all students
     * @return page of students
     */
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Student> search(String like,
                                Long trackId,
                                Integer course,
                                Integer groupNumber,
                                Boolean hasTeam,
                                Boolean isCaptain,
                                List<Long> technologies,
                                Pageable pageable) {

        Specification<Student> spec = (root, query, cb) -> cb.conjunction();

        if (like != null) {
            spec = spec.and(StudentSpecification.like(like));
        }
        if (trackId != null) {
            spec = spec.and(StudentSpecification.byTrack(trackId));
        }
        if (course != null) {
            spec = spec.and(StudentSpecification.byCourse(course));
        }
        if (groupNumber != null) {
            spec = spec.and(StudentSpecification.byGroup(groupNumber));
        }
        if (hasTeam != null) {
            spec = spec.and(StudentSpecification.byHasTeam(hasTeam));
        }
        if (isCaptain != null) {
            spec = spec.and(StudentSpecification.byIsCaptain(isCaptain));
        }
        if (technologies != null && !technologies.isEmpty()) {
            spec = spec.and(StudentSpecification.hasTechnologies(technologies));
        }

        Sort sort = pageable.getSort();
        for (Sort.Order order : sort) {
            if ("name".equals(order.getProperty())) {
                pageable = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(order.getDirection(), "user.fio")
                );
                break;
            }
        }

        return studentRepository.findAll(spec, pageable);
    }


    /**
     * Creates student using given data.
     * @param dto DTO containing student data
     * @return created student
     */
    @Transactional
    public Student create(StudentCreationDto dto) {
        User user = userService.findByIdOrElseThrow(dto.getUserId());
        var role = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new NotFoundException("Роль STUDENT не найдена"));
        user.setRole(role);

        Student student = studentCreationDtoMapper.mapToEntity(dto);
        student.setUser(user);
        return studentRepository.save(student);
    }

    /**
     * Deletes student entity
     * @param id student id
     * @throws NotFoundException in case there is no student with such id
     */
    @Transactional
    public void delete(Long id) {
        Student st = findByIdOrElseThrow(id);
        if (Boolean.TRUE.equals(st.getHasTeam())) {
            teamService.removeStudentFromTeam(st.getCurrentTeam(), st);
        }
        studentRepository.delete(st);
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
        Student st = findByIdOrElseThrow(id);

        if (Boolean.TRUE.equals(isUnsafeAllowed)) {
            st.setHasTeam(dto.getHasTeam());
            st.setIsCaptain(dto.getIsCaptain());
        }
        st.setCourse(dto.getCourse());
        st.setGroupNumber(dto.getGroupNumber());
        st.setAboutSelf(dto.getAboutSelf());
        st.setContacts(dto.getContacts());
        st.setTechnologies(technologyDtoMapper.mapListToEntity(dto.getTechnologies()));
        Long newTeamId = dto.getCurrentTeam().getId();
        if (newTeamId != null) {
            Team newTeam = teamService.findByIdOrElseThrow(newTeamId);

            st.setCurrentTeam(newTeam);

            if (st.getTeams().stream().noneMatch(t -> t.getId().equals(newTeamId))) {
                st.getTeams().add(newTeam);
            }
        } else {
            st.setCurrentTeam(null);
        }

        return studentRepository.save(st);
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
