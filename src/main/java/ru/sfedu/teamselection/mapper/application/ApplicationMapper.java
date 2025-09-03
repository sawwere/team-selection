package ru.sfedu.teamselection.mapper.application;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.sfedu.teamselection.domain.application.Application;
import ru.sfedu.teamselection.domain.application.TeamInvite;
import ru.sfedu.teamselection.domain.application.TeamRequest;
import ru.sfedu.teamselection.dto.application.ApplicationCreationDto;
import ru.sfedu.teamselection.dto.application.ApplicationDto;
import ru.sfedu.teamselection.dto.application.ApplicationResponseDto;
import ru.sfedu.teamselection.enums.ApplicationStatus;

@Mapper(
        componentModel       = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ApplicationMapper {

    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    // Создание сущности из DTO при запросе на создание
    @Mapping(source = "teamId",    target = "team.id")
    @Mapping(source = "studentId", target = "student.id")
    @Mapping(target = "status",    qualifiedByName = "mapStatus")
    Application mapCreationToEntity(ApplicationCreationDto dto);

    @InheritInverseConfiguration(name = "mapCreationToEntity")
    @Mapping(target = "status", qualifiedByName = "mapStatus")
    ApplicationCreationDto mapToCreationDto(Application entity);

    @InheritInverseConfiguration(name = "mapCreationToEntity")
    @Mapping(target = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "possibleTransitions", ignore = true)
    ApplicationResponseDto mapToResponseDto(Application entity);

    // Преобразование Entity -> DTO
    @Mapping(source = "id", target = "id")
    @Mapping(source = "team.id", target = "team.id")
    @Mapping(source = "team.name", target = "team.name")
    // Для поля fio берем значение из student.user.fio напрямую
    @Mapping(source = "student.user.fio", target = "student.fio")
    @Mapping(source = "student.user.id", target = "student.userId")
    @Mapping(source = "student.id", target = "student.id")
    @Mapping(source = "student.course", target = "student.course")
    @Mapping(source = "student.groupNumber", target = "student.groupNumber")
    @Mapping(source = "student.aboutSelf",  target = "student.aboutSelf")
    @Mapping(source = "student.contacts",   target = "student.contacts")
    @Mapping(target = "status", qualifiedByName = "mapStatus")
    ApplicationDto mapToDto(Application entity);

    @InheritInverseConfiguration(name = "mapToDto")
    @Mapping(target = "status", qualifiedByName = "mapStatus")
    Application mapToEntity(ApplicationDto dto);

    @Named("mapStatus")
    default ApplicationStatus mapStatus(String status) {
        return ApplicationStatus.of(status);
    }

    @Named("mapStatus")
    default String mapStatus(ApplicationStatus status) {
        return status.toString();
    }

    @ObjectFactory
    default Application createForCreation(ApplicationCreationDto dto) {
        return switch (dto.getType()) {
            case INVITE  -> new TeamInvite();
            case REQUEST -> new TeamRequest();
        };
    }

    @ObjectFactory
    default Application createForDto(ApplicationDto dto) {
        return switch (dto.getType()) {
            case INVITE  -> new TeamInvite();
            case REQUEST -> new TeamRequest();
        };
    }
}
