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
import ru.sfedu.teamselection.enums.ApplicationStatus;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ApplicationMapper {
    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    @Mapping(source = "teamId", target = "team.id")
    @Mapping(source = "studentId", target = "student.id")
    @Mapping(target = "status", qualifiedByName = "mapStatus")
    Application mapToEntity(ApplicationCreationDto applicationCreationDto);

    @InheritInverseConfiguration(name = "mapToEntity")
    @Mapping(target = "status", qualifiedByName = "mapStatus")
    ApplicationCreationDto mapToDto(Application application);


    @Named("mapStatus")
    default ApplicationStatus mapStatus(String status) {
        return ApplicationStatus.of(status);
    }

    @Named("mapStatus")
    default String mapStatus(ApplicationStatus status) {
        return status.toString();
    }

    @ObjectFactory
    default Application mapApplicationByType(ApplicationCreationDto creationDto) {
        switch (creationDto.getType()) {
            case INVITE -> {
                return new TeamInvite();
            }
            case REQUEST -> {
                return new TeamRequest();
            }
            default -> throw new IllegalStateException("Unexpected value: " + creationDto.getType());
        }
    }
}
