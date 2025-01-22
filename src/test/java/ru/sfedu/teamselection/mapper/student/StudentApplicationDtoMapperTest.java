package ru.sfedu.teamselection.mapper.student;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.application.StudentApplicationDto;
import ru.sfedu.teamselection.mapper.application.StudentApplicationDtoMapper;

class StudentApplicationDtoMapperTest {

    private final StudentApplicationDtoMapper underTest = new StudentApplicationDtoMapper();


    @Test
    void mapToEntity() {
        StudentApplicationDto dto = StudentApplicationDto.builder()
                .id(22L)
                .fio("f i o")
                .course(1)
                .groupNumber(10)
                .userId(1L)
                .aboutSelf("about self")
                .contacts("tg 80@a")
                .build();

        Student expected = Student.builder()
                .id(22L)
                .course(dto.getCourse())
                .groupNumber(dto.getGroupNumber())
                .aboutSelf(dto.getAboutSelf())
                .contacts(dto.getContacts())
                .currentTeam(null)
                .user(null)
                .build();

        Student actual = underTest.mapToEntity(dto);
        Assertions.assertEquals(expected.getCourse(), actual.getCourse());
        Assertions.assertEquals(expected.getGroupNumber(), actual.getGroupNumber());
        Assertions.assertEquals(expected.getAboutSelf(), actual.getAboutSelf());
        Assertions.assertEquals(expected.getContacts(), actual.getContacts());
        Assertions.assertEquals(expected.getCurrentTeam(), actual.getCurrentTeam());
    }

    @Test
    void mapToDto() {
        Student entity = Student.builder()
                .id(666L)
                .course(1)
                .groupNumber(11)
                .aboutSelf("about_self")
                .contacts("dto.getContacts()")
                .currentTeam(Team.builder()
                        .id(1L)
                        .build())
                .user(User.builder()
                        .id(1L)
                        .fio("f i o")
                        .email("example@example.com")
                        .role(Role.builder()
                                .id(1L)
                                .name("ADMIN")
                                .build())
                        .isRemindEnabled(true)
                        .build())
                .build();

        StudentApplicationDto expected = StudentApplicationDto.builder()
                .id(entity.getId())
                .fio(entity.getUser().getFio())
                .course(entity.getCourse())
                .groupNumber(entity.getGroupNumber())
                .userId(entity.getUser().getId())
                .aboutSelf(entity.getAboutSelf())
                .contacts(entity.getContacts())
                .build();

        StudentApplicationDto actual = underTest.mapToDto(entity);
        Assertions.assertEquals(expected.getCourse(), actual.getCourse());
        Assertions.assertEquals(expected.getGroupNumber(), actual.getGroupNumber());
        Assertions.assertEquals(expected.getAboutSelf(), actual.getAboutSelf());
        Assertions.assertEquals(expected.getContacts(), actual.getContacts());
    }
}