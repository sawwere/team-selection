package ru.sfedu.teamselection.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final String frontendLoginUrl;

    public CustomAuthenticationEntryPoint(@Value("${frontend.login.url}") String frontendLoginUrl) {
        this.frontendLoginUrl = frontendLoginUrl;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
        String requestedUri = request.getRequestURI();

        if (requestedUri.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
        } else {
            response.sendRedirect(frontendLoginUrl);
        }
    }
}
