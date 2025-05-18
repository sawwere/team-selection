package ru.sfedu.teamselection.config;

import java.util.Arrays;
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
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.sfedu.teamselection.config.security.SimpleAuthenticationSuccessHandler;
import ru.sfedu.teamselection.service.security.AzureOidcUserService;
import ru.sfedu.teamselection.service.security.Oauth2UserService;
import ru.sfedu.teamselection.util.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final Oauth2UserService oauth2UserService;
    private final AzureOidcUserService oidcUserService;
    private final SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;
    private static final String ADMIN_ROLE_NAME = "ADMIN";
    public static final String LOGOUT_URL = "/api/v1/auth/logout";

    @Value("${frontend.url}")
    private String frontendUrl;
    @Value("${frontend.login.url}")
    private String frontendLoginUrl;

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/login", "/registration").anonymous()
                        .requestMatchers(HttpMethod.DELETE).hasRole(ADMIN_ROLE_NAME)
                        .requestMatchers("/actuator/prometheus")
                            .access(new WebExpressionAuthorizationManager("hasIpAddress('10.5.0.55')"))
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/api/v1/tracks").permitAll()
                        .anyRequest().permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .deleteCookies("JSESSIONID", "SessionId")
                        .logoutSuccessUrl(frontendUrl + "/login")
                )
                .oauth2Login(login -> login
                       // .loginPage("/oauth2/authorization/azure")
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(oauth2UserService)
                                .oidcUserService(oidcUserService)
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
        corsConfiguration.setAllowedOrigins(List.of(frontendUrl)); // Explicitly define allowed origin
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"
        ));
        // Headers you expose to the frontend
        corsConfiguration.setExposedHeaders(Arrays.asList("Content-Type", "Authorization"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}

