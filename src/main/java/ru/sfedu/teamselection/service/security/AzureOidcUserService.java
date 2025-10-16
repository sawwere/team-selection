package ru.sfedu.teamselection.service.security;


import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.exception.ForbiddenException;
import ru.sfedu.teamselection.repository.RoleRepository;
import ru.sfedu.teamselection.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class AzureOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        String azureOid = oidcUser.getAttribute("oid");

        // найдём или создадим пользователя в БД
        User user = userRepository.findByEmailFetchRole(email)
                .orElseGet(() -> {
                    User u = User.builder()
                            .fio(name)
                            .email(email)
                            .isEnabled(true)
                            .role(roleRepository.findById(1L).orElseThrow())
                            .azureId(azureOid)
                            .build();
                    return userRepository.save(u);
                });

        if (!user.isEnabled()) {
            throw new ForbiddenException(
                    "Аккаунт отключен. По вопросам возвращения доступа обращаться к администратору ресурса."
            );
        }
        // ****** вот здесь собираем authorities ******
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        // 1) все authority, которые пришли в токене (OIDC scopes и т.п.)
        mappedAuthorities.addAll(oidcUser.getAuthorities());

        // 2) добавляем роль из БД (Spring ожидает префикс "ROLE_")
        String roleName = user.getRole().getName();
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

        // возвращаем DefaultOidcUser с новыми authorities
        return new DefaultOidcUser(
                mappedAuthorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }
}


