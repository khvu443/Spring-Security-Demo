package com.example.springsecurity.service;

import com.example.springsecurity.auth.AuthenticationResponse;
import com.example.springsecurity.auth.RegisterRequest;
import com.example.springsecurity.model.Role;
import com.example.springsecurity.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    AuthenticationResponse saveUser(RegisterRequest u) throws Exception;
    Role saveRole(Role r);
    Optional<User> getUser(String username);
    List<User> getAllUser();

    Optional<User> findUserBy(int id);

}
