package com.example.springsecurity.auth;

import com.example.springsecurity.config.JwtService;
import com.example.springsecurity.model.Role;
import com.example.springsecurity.model.User;
import com.example.springsecurity.repository.RoleRepository;
import com.example.springsecurity.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRep;
    private final RoleRepository roleRep;
    private final PasswordEncoder encoder;
    private final JwtService service;
    private final AuthenticationManager manager;
    private final UserDetailsService userDetailService;


    public AuthenticationResponse register(RegisterRequest request) throws Exception {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .username(request.getUsername())
                .roles(new ArrayList<>())
                .build();
        try {

            if (userRep.findByEmail(user.getEmail()).isPresent()) {
                log.error("Exist email {} in DB", user.getEmail());
                log.info("info {}", userRep.findByEmail(user.getEmail()));
                return null;
            } else {
                Collection<String> roleStr = request.getRoles();
                List<Role> role = new ArrayList<>();
                roleStr.forEach(str ->
                {
                    role.add(roleRep.findByName(str));
                });
                log.info("List role register: {}", role);
                role.forEach(r -> user.getRoles().add(r));

                userRep.save(user);
                var jwtToken = service.generateToken(user);
                var jwtRefresh = service.refreshToken(user);

                log.info("Get getAuthorities: {}", user.getAuthorities());
                log.info("Register Token access: {}", jwtToken);
                log.info("Register Token refresh: {}", jwtRefresh);
                return new AuthenticationResponse(jwtToken, jwtRefresh);
            }
        } catch (Exception e) {
            log.error("Something wrong happen: {}", e.getMessage());
            userRep.delete(user);
        }
        return null;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
/*//        manager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );*/
        var user = userRep.findByEmail(request.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("NOT FOUND")
        );
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        log.info("Check same password {}", encoder.matches(request.getPassword(), user.getPassword()));
        if (user == null || !encoder.matches(request.getPassword(), user.getPassword())) {
            log.info("Login Fail service");
            return new AuthenticationResponse();
        } else {
            var jwtToken = service.generateToken(user);
            var jwtRefresh = service.refreshToken(user);
            log.info("Login Token access: {}", jwtToken);
            log.info("Login Token refresh: {}", jwtRefresh);
            return new AuthenticationResponse(jwtToken, jwtRefresh);
        }
    }

    public AuthenticationResponse refreshNewToken
            (HttpServletRequest request,
             HttpServletResponse response) throws IOException {

        log.info("Refresh token to make new access token and refresh token");
//        String authHeader = request.getHeader("Authorization");
        String authHeader = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    authHeader = cookie.getValue();
                    log.info("Get token refresh from cookie {}", authHeader);
                }
            }
        }
        String jwt;
        String userEmail;

        try {
            jwt = authHeader;
            userEmail = service.extractUsername(jwt);
            log.info("refresh token get: {}", userEmail);
            if (!userEmail.isEmpty()) {
                UserDetails user = userDetailService.loadUserByUsername(userEmail);
                log.info("refresh user: {} ", user);
                log.debug("check valid token refresh");
                if (service.isTokenValid(jwt, user)) {
                    log.info("Access {} and refresh {}", service.generateToken(user), service.refreshToken(user));
                    return new AuthenticationResponse(service.generateToken(user), service.refreshToken(user));
                }
            }
        } catch (Exception e) {
            log.error("Something wrong happen");
            response.setHeader("ERROR", e.getMessage());
            response.setStatus(FORBIDDEN.value());
            Map<String, String> error = new HashMap<>();
            error.put("ERROR_MSG", e.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }

        return new AuthenticationResponse();
    }

    public void saveToCookie(AuthenticationResponse authRes, HttpServletResponse response) {
        log.info("save {} to HttpOnly Cookie", authRes);
        Cookie jwtAccess = new Cookie("access", authRes.getToken());
        jwtAccess.setMaxAge(10 * 60);
        jwtAccess.setHttpOnly(true);
        jwtAccess.setPath("/");

        response.addCookie(jwtAccess);

        Cookie jwtRefresh = new Cookie("refresh", authRes.getRefresh());
        jwtRefresh.setMaxAge(20 * 60);
        jwtRefresh.setHttpOnly(true);
        jwtRefresh.setPath("/");

        response.addCookie(jwtRefresh);
    }

    public User isUserExist(String email) {
        log.info("check {} if exist or not", userRep.findAll());
        User[] user = {null};
        if (userRep.findAll().size() == 0) {
            return null;
        } else {
            userRep.findByEmail(email).ifPresent(
                    (User u) -> {
                        user[0] = u;
                    }

            );
        }
        return user[0];
    }
}
