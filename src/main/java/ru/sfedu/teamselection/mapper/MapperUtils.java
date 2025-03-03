package ru.sfedu.teamselection.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.sfedu.teamselection.domain.Role;
import ru.sfedu.teamselection.repository.RoleRepository;

@Component
@RequiredArgsConstructor
public class MapperUtils {
    private final RoleRepository roleRepository;

    @Named("mapRoleNameToRole")
    public Role mapRoleNameToRole(String roleName) {
        return roleRepository.findByName(roleName).orElseThrow();
    }

    @Named("mapRoleToString")
    public String mapRoleToString(Role role) {
        return role.getName();
    }
}
