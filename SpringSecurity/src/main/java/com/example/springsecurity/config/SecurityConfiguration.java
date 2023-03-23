package com.example.springsecurity.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception {
        https
                .csrf()
                .disable()

                //Authorize unrestricted when go to log in(authenticate) or register
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**", "/js/**", "/css/**").permitAll()

                .and()
                //Restricted and only user role can pass
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/user/**").hasAnyAuthority("ROLE_USER", "ROLE_MANAGER")

                .and()
                //Restricted and only manager role can pass
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/manager/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_USER")
//
                .and()
                //Restricted and only admin role can pass
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ROLE_ADMIN")

                .anyRequest()
                .authenticated()

                .and()
                .formLogin()
                .loginPage("/api/v1/auth/authenticateForm")
                .usernameParameter("email").passwordParameter("password")

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return https.build();
    }


}
