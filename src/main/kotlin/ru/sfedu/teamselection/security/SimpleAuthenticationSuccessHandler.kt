package ru.sfedu.teamselection.security

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import ru.sfedu.teamselection.domain.Users
import ru.sfedu.teamselection.enums.Roles
import ru.sfedu.teamselection.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class SimpleAuthenticationSuccessHandler(

) : SimpleUrlAuthenticationSuccessHandler() {

    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val user = authentication.principal as DefaultOidcUser
        redirectStrategy.sendRedirect(request, response, "https://pdflutterweb.web.app/?token=${user.idToken.tokenValue}")

    }
}