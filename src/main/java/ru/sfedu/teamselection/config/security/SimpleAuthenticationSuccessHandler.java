package ru.sfedu.teamselection.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class SimpleAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        var user = authentication.getPrincipal();
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

        redirectStrategy.sendRedirect(request, response, "http://localhost:5173/teams");
    }
}
