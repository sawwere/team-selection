package ru.sfedu.teamselection.service

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import ru.sfedu.teamselection.domain.Users
import ru.sfedu.teamselection.enums.Roles
import ru.sfedu.teamselection.repository.UserRepository

@Service
class Oauth2UserService(
    private val userRepository: UserRepository
): DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = super.loadUser(userRequest) as DefaultOidcUser
        val email = user.email
        val userInDb = userRepository.findByEmail(email)
        if (userInDb == null) {
            userRepository.save(Users(fio = user.fullName, email = email, role = Roles.USER, registered = false))
        }
        return user
    }
}