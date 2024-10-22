package ru.sfedu.teamselection.service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.enums.Roles;
import ru.sfedu.teamselection.repository.UserRepository;

@Service
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public Oauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public DefaultOidcUser loadUser(OAuth2UserRequest userRequest) {
        DefaultOidcUser user = (DefaultOidcUser) super.loadUser(userRequest);
        String email = user.getEmail();
        User userInDb = userRepository.findByEmail(email);

        if (userInDb == null) {
            User newUser = new User();
            newUser.setFio(user.getFullName());
            newUser.setEmail(email);
            newUser.setRole(Roles.USER);
            newUser.setIsEnabled(false);
            userRepository.save(newUser);
        }

        return user;
    }
}

