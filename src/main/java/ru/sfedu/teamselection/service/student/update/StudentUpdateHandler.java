package ru.sfedu.teamselection.service.student.update;

import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.dto.StudentUpdateDto;

public interface StudentUpdateHandler {
    void update(Student student, StudentUpdateDto dto);
}
