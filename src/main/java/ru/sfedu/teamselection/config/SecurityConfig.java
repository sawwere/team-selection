package ru.sfedu.teamselection.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import ru.sfedu.teamselection.config.security.SimpleAuthenticationSuccessHandler;
import ru.sfedu.teamselection.service.Oauth2UserService;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.sfedu.teamselection.util.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final Oauth2UserService oauth2UserService;
    private final SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final String frontendLoginUrl = "http://localhost:5173/login";


    private static final String ADMIN_ROLE_NAME = "ADMIN";
    public static final String LOGOUT_URL = "/api/v1/auth/logout";

    @Value("${app.frontendUrl}")
    private String frontendUrl;

    public SecurityConfig(Oauth2UserService oauth2UserService,
                          SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.oauth2UserService = oauth2UserService;
        this.simpleAuthenticationSuccessHandler = simpleAuthenticationSuccessHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/login", "/registration").anonymous()
                        .requestMatchers(HttpMethod.DELETE).hasAuthority(ADMIN_ROLE_NAME)
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .deleteCookies("JSESSIONID", "SessionId")
                        .logoutSuccessUrl(frontendUrl + "/login")
                )

                .oauth2Login(login -> login
                        .userInfoEndpoint(endpoint ->
                                endpoint.userService(oauth2UserService)
                        )
                        .successHandler(simpleAuthenticationSuccessHandler)
                )

                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(frontendLoginUrl))
                );

        return http.build();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true); // Required for session/cookie-based auth
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173")); // Explicitly define allowed origin
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"
        ));
        corsConfiguration.setExposedHeaders(Arrays.asList("Content-Type", "Authorization")); // Headers you expose to the frontend
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Only HTTP methods
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }





}

