package ru.sfedu.teamselection.mapper.user;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.dto.UserDto;
import ru.sfedu.teamselection.dto.student.StudentSummaryDto;
import ru.sfedu.teamselection.exception.RoleNotFoundException;
import ru.sfedu.teamselection.repository.RoleRepository;

@Mapper(
        componentModel       = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    protected RoleRepository roleRepository;

    /**
     * Полное маппинг User → UserDto, включая сводку по студенту.
     */
    @Mapping(source = "role",    target = "role",    qualifiedByName = "roleToString")
    @Mapping(source = "student", target = "student", qualifiedByName = "toStudentSummary")
    public abstract UserDto mapToDto(User user);

    /**
     * Обратный маппинг UserDto → User: роль конвертируется, а student просто игнорируется,
     * поскольку с фронта мы его не правим.
     */
    @Mapping(target = "student", ignore = true)
    @Mapping(source = "role",    target = "role",    qualifiedByName = "stringToRole")
    public abstract User mapToEntity(UserDto dto);

    /**
     * Квалифицированный метод, формирующий StudentSummaryDto из Student,
     * берёт только нужные поля.
     */
    @Named("toStudentSummary")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "course",           target = "course")
    @Mapping(source = "groupNumber",      target = "groupNumber")
    @Mapping(source = "currentTeam.id",   target = "currentTeamId")
    @Mapping(source = "currentTeam.name", target = "currentTeamName")
    @Mapping(source = "currentTrack.id", target = "currentTrackId")
    protected abstract StudentSummaryDto toStudentSummary(Student s);

    /**
     * Role → String
     */
    @Named("roleToString")
    protected String roleToString(Role r) {
        return r.getName();
    }

    /**
     * String → Role
     */
    @Named("stringToRole")
    protected Role stringToRole(String name) {
        return roleRepository
                .findByName(name)
                .orElseThrow(() -> new RoleNotFoundException(name));
    }
}
