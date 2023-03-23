package com.example.springsecurity.auth;

import com.example.springsecurity.model.User;
import com.example.springsecurity.repository.UserRepository;
import com.google.gson.Gson;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserDetailsService userDetailService;
    private final UserRepository userRep;

    private ModelAndView mav = new ModelAndView();

    @GetMapping("/checkEmail")
    public @ResponseBody String isEmailExist(HttpServletRequest request) {
        String email = request.getParameter("email");
        log.info("Check email from ajax: {}", email);
        log.info("Get email check in DB -> {}", service.isUserExist(email));

        User u = service.isUserExist(email);

        String res = new Gson().toJson(u);
        log.info("Data after get by user name {}", res);
        return res;
    }

    @GetMapping("/registerForm")
    public ModelAndView registerForm() {

        mav.setViewName("register");
        return mav;
    }

    @PostMapping("/register")
    public ModelAndView register(
            @ModelAttribute RegisterRequest request,
            HttpServletResponse response
    ) throws Exception {
        log.info("Register info : {}", request);
        AuthenticationResponse responseReg = service.register(request);

        log.info("Register Success");
        service.saveToCookie(responseReg, response);
        mav.setViewName("redirect:/api/v1/manager/displayAll");
        return mav;
    }


    @GetMapping("/authenticateForm")
    public ModelAndView authenticateForm() {

        mav.setViewName("login");
        return mav;
    }

    @PostMapping("/authenticate")
    public ModelAndView authenticate(
            @ModelAttribute AuthenticationRequest request, HttpServletResponse response
    ) {
        AuthenticationResponse responseLogin = service.authenticate(request);
        log.info("The token from login {}", responseLogin);

        Cookie cookie = new Cookie("uid", String.valueOf(userRep.findByEmail(request.getEmail()).get().getId()));
        cookie.setMaxAge(10 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);

        if (responseLogin.getRefresh() == null && responseLogin.getToken() == null) {
            log.error("Login fail controller");
        } else {
            log.info("Login Success");
            service.saveToCookie(responseLogin, response);

            mav.setViewName("redirect:/api/v1/manager/displayAll");
        }
        return mav;
    }

    @GetMapping("/refresh")
    public ModelAndView refreshToken(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response) throws IOException {

        boolean isReload = Boolean.parseBoolean(request.getParameter("reload"));
        log.info("The page is reload - {} - for refresh ", request.getParameter("reload"));
        log.info("Refresh token");

        if (isReload) {
            AuthenticationResponse responseRefresh = service.refreshNewToken(request, response);
            log.info("Refresh token {} in controller", responseRefresh);

            if (responseRefresh == null) {
                log.info("Token refresh is expired");
                mav.setViewName("redirect:/api/v1/auth/authenticateForm");
            } else {
                log.info("Refresh new token access and refresh");
                service.saveToCookie(responseRefresh, response);
                mav.setViewName("redirect:/api/v1/manager/displayAll");
            }
        }
        return mav;
    }
}
