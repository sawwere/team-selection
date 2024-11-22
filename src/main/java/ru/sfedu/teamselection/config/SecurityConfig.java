package ru.sfedu.teamselection.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import ru.sfedu.teamselection.config.security.SimpleAuthenticationSuccessHandler;
import ru.sfedu.teamselection.service.Oauth2UserService;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final Oauth2UserService oauth2UserService;
    private final SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;

    private static final String ADMIN_ROLE_NAME = "ADMIN";
    public static final String LOGOUT_URL = "/api/v1/auth/logout";

    @Value("${app.frontendUrl}")
    private String frontendUrl;

    public SecurityConfig(Oauth2UserService oauth2UserService,
                          SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler) {
        this.oauth2UserService = oauth2UserService;
        this.simpleAuthenticationSuccessHandler = simpleAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/error", "/login").permitAll()
                        .requestMatchers(HttpMethod.DELETE).hasAuthority(ADMIN_ROLE_NAME)
                        .anyRequest().authenticated())
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL))
                .oauth2Login(login -> login
                        .userInfoEndpoint(endpoint ->
                                endpoint.userService(oauth2UserService)
                        )
                        .successHandler(simpleAuthenticationSuccessHandler)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(frontendUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("SessionId")); // Разрешаем браузеру видеть эти заголовки
        configuration.setAllowCredentials(true); // Разрешаем отправку cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}

