package ru.sfedu.teamselection.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.sfedu.teamselection.security.SimpleAuthenticationSuccessHandler
import ru.sfedu.teamselection.service.Oauth2UserService

//TODO update using new SpringSecurity Config
@Configuration
class SecurityConfig(
    private val oauth2UserService: Oauth2UserService,
    private val simpleAuthenticationSuccessHandler: SimpleAuthenticationSuccessHandler
)
    //: WebSecurityConfigurerAdapter() {
//    override fun configure(http: HttpSecurity) {
//        http
//            .csrf { it.disable() }
//            .cors { it.configurationSource(corsConfigurationSource()) }
//            .authorizeHttpRequests()
//            .antMatchers("/swagger-ui/**")
//            .permitAll()
//            .anyRequest().permitAll()
//            .and()
//            .oauth2Login()
//            .loginPage("/oauth2/authorization/azure")
//            .userInfoEndpoint()
//            .userService(oauth2UserService)
//            .and()
//            .successHandler(simpleAuthenticationSuccessHandler)
//    }

//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource {
//        val configuration = CorsConfiguration()
//        configuration.allowedOrigins = listOf("*")
//        configuration.addAllowedHeader("*")
//        configuration.addAllowedMethod("*")
//        val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
//        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration)
//        return urlBasedCorsConfigurationSource
//    }
//}