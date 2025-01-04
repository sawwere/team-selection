package ru.sfedu.teamselection.service.security;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.repository.RoleRepository;
import ru.sfedu.teamselection.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User loadUser(OAuth2UserRequest userRequest) {
        var oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String login = oAuth2User.getAttribute("login");
        //TODO Пока так, ибо в гитхабе почта приватная по умолчанию
        Optional<User> userInDb = userRepository.findByEmailFetchRole(login);

        if (userInDb.isEmpty()) {
            User newUser = User.builder()
                    .fio(login)
                    .email(login)
                    .isEnabled(true)
                    .role(roleRepository.findById(1L).orElseThrow())
                    .build();
            return userRepository.save(newUser);
        }

        return userInDb.get();
    }
}


