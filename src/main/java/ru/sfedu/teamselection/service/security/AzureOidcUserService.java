package ru.sfedu.teamselection.service.security;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.repository.RoleRepository;
import ru.sfedu.teamselection.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class AzureOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        var oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        Optional<User> userInDb = userRepository.findByEmailFetchRole(email);

        if (userInDb.isEmpty()) {
            User newUser = User.builder()
                    .fio(name)
                    .email(email)
                    .isEnabled(true)
                    .role(roleRepository.findById(1L).orElseThrow())
                    .build();

            return new OidcUserImpl(oAuth2User.getAuthorities(),
                    oAuth2User.getIdToken(),
                    oAuth2User.getUserInfo(),
                    userRepository.save(newUser)
            );
        }

        return new OidcUserImpl(oAuth2User.getAuthorities(),
                oAuth2User.getIdToken(),
                oAuth2User.getUserInfo(),
                userInDb.orElseThrow()
                );
    }
}


