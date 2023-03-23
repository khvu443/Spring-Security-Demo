package com.example.springsecurity.controller;

import com.example.springsecurity.auth.RegisterRequest;
import com.example.springsecurity.model.User;
import com.example.springsecurity.service.UserServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class DemoController {
    @Autowired
    private final UserServiceImpl service;

    private ModelAndView mav = new ModelAndView();

    @GetMapping("/user/displayUser/{username}")
    public ResponseEntity<Optional<User>> displayUser(@PathVariable(value = "username") String username) {
        return ResponseEntity.ok().body(service.getUser(username));
    }

    @GetMapping("/manager/displayAll")
    public ModelAndView showAll(HttpServletRequest request) {

        log.info("Display all user ");
        String access = "";
        String id = "";

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equalsIgnoreCase("access")) {
                    access = c.getValue();
                }
                if (c.getName().equalsIgnoreCase("uid")) {
                    id = c.getValue();
                }
            }
        }

        if (access != null) {
            mav.addObject("username", service.findUserBy(Integer.parseInt(id)).get().getUsername());
            mav.addObject("username", service.findUserBy(Integer.parseInt(id)).get().getRoles());
            mav.addObject("users",service.getAllUser());
            mav.setViewName("demo");
        }
        return mav;
    }

    @GetMapping("/admin/updateUser")
    public ResponseEntity<String> updateUser() {
        return ResponseEntity.ok("Update Success User");
    }

    @GetMapping("/admin/saveRole")
    public ResponseEntity<String> saveRole() {
        return ResponseEntity.ok("Save Success Role");
    }

    @GetMapping("/admin/addForm")
    public ModelAndView addForm()
    {
        mav.setViewName("AddUser");
        return mav;
    }
    @PostMapping("/admin/addUser")
    public ModelAndView addUser(
            @ModelAttribute RegisterRequest request
    ) throws Exception {
        log.info("Add user: ", request.getEmail());
        service.saveUser(request);
        mav.setViewName("redirect:/api/v1/manager/displayAll");
        return mav;
    }
}
