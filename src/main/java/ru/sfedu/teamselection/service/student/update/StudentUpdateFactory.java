package ru.sfedu.teamselection.service.student.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.mapper.TechnologyMapper;
import ru.sfedu.teamselection.service.TeamService;
import ru.sfedu.teamselection.service.TrackService;
import ru.sfedu.teamselection.service.security.PermissionLevelUpdate;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentUpdateFactory {
    protected final TeamService teamService;
    protected final TrackService trackService;
    protected final TechnologyMapper technologyDtoMapper;

    public StudentUpdateHandler getHandler(PermissionLevelUpdate permission) {
        return switch (permission) {
            case ADMIN -> new StudentUpdateAdminHandler(teamService, technologyDtoMapper, trackService);
            case OWNER -> new StudentUpdateOwnerHandler(teamService, technologyDtoMapper);
            default -> throw new ForbiddenException("Cannot update student using given permission: " + permission);
        };
    }
}
