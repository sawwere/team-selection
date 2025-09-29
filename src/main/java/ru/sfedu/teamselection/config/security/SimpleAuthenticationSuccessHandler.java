package ru.sfedu.teamselection.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.service.UserService;

@Component
@RequiredArgsConstructor
public class SimpleAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Value("${frontend.url}")
    private String frontendUrl;

    private final UserService userService;

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        //var oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2User oidcUser = (OAuth2User) authentication.getPrincipal();
        String email = oidcUser.getAttribute("email");  // или preferred_username

        // подгружаем из БД
        User user = userService.findByEmail(email);



        String sessionId = ((WebAuthenticationDetails) authentication.getDetails()).getSessionId();

        Cookie sessionCookie = new Cookie("SessionId", sessionId);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(sessionCookie);

        Cookie jSessionIdCookie = WebUtils.getCookie(request, "JSESSIONID");
        if (jSessionIdCookie == null) {
            jSessionIdCookie = new Cookie("JSESSIONID", request.getSession().getId());
            jSessionIdCookie.setHttpOnly(true);
            jSessionIdCookie.setSecure(true);
            jSessionIdCookie.setPath("/");
            jSessionIdCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(jSessionIdCookie);
        }

        //User user;
        //if (oAuth2User instanceof OidcUserImpl oidcUser) {
            //user = oidcUser.getUser();
        //} else {
            //user = (User) oAuth2User;
        //}

        if (user.getRole().getName().contains("STUDENT")) {
            redirectStrategy.sendRedirect(request, response, frontendUrl + "/teams");
        } else {
            redirectStrategy.sendRedirect(request, response, frontendUrl + "/registration");
        }
    }


}
