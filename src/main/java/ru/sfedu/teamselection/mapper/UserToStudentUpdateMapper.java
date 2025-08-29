package ru.sfedu.teamselection.mapper;


import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.sfedu.teamselection.dto.StudentUpdateDto;
import ru.sfedu.teamselection.dto.StudentUpdateTeamDto;
import ru.sfedu.teamselection.dto.StudentUpdateTrackDto;
import ru.sfedu.teamselection.dto.StudentUpdateUserDto;
import ru.sfedu.teamselection.dto.UserDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserToStudentUpdateMapper {

    UserToStudentUpdateMapper INSTANCE = Mappers.getMapper(UserToStudentUpdateMapper.class);

    @Mapping(source = "userDto", target = "user")
    @Mapping(source = "userDto.student", target = ".")
    @Mapping(source = "userDto.student.currentTeamId", target = "currentTeam", qualifiedByName = "mapTeamIdToTeamDto")
    @Mapping(
            source = "userDto.student.currentTrackId",
            target = "currentTrack",
            qualifiedByName = "mapTrackIdToTrackDto"
    )
    StudentUpdateDto userDtoToStudentUpdateDto(UserDto userDto);

    @Named("mapTeamIdToTeamDto")
    default StudentUpdateTeamDto mapTeamIdToTeamDto(Long teamId) {
        if (teamId == null) {
            return null;
        }
        StudentUpdateTeamDto teamDto = new StudentUpdateTeamDto();
        teamDto.setId(teamId);
        return teamDto;
    }

    @Named("mapTrackIdToTrackDto")
    default StudentUpdateTrackDto mapTrackIdToTrackDto(Long trackId) {
        if (trackId == null) {
            return null;
        }
        StudentUpdateTrackDto trackDto = new StudentUpdateTrackDto();
        trackDto.setId(trackId);
        return trackDto;
    }

    // Map UserDto to StudentUpdateUserDto
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fio", source = "fio")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "isRemindEnabled", source = "isRemindEnabled")
    @Mapping(target = "isEnabled", source = "isEnabled")
    StudentUpdateUserDto userDtoToStudentUpdateUserDto(UserDto userDto);

    @AfterMapping
    default void setAdditionalFields(@MappingTarget StudentUpdateDto dto, UserDto source) {
        dto.setTechnologies(null);
    }
}
