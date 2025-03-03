package ru.sfedu.teamselection.exception;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(String roleName) {
        super("Role not found: " + roleName);
    }
}
