package com.example.springsecurity.service;

import com.example.springsecurity.auth.AuthenticationResponse;
import com.example.springsecurity.auth.AuthenticationService;
import com.example.springsecurity.auth.RegisterRequest;
import com.example.springsecurity.model.Role;
import com.example.springsecurity.model.User;
import com.example.springsecurity.repository.RoleRepository;
import com.example.springsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepository userRep;
    private final RoleRepository roleRep;
    private final AuthenticationService service;

    @Override
    public AuthenticationResponse saveUser(RegisterRequest u) throws Exception {
        log.info("Add user or save change of user");
        return service.register(u);
    }

    @Override
    public Role saveRole(Role r) {
        log.info("Add more role");
        return roleRep.save(r);
    }

    @Override
    public Optional<User> getUser(String username) {
        log.info("Get info of user {}",username);
        return this.userRep.findByEmail(username);
    }

    @Override
    public List<User> getAllUser() {
        log.info("Get all user from DB: {}", userRep.findAll());
        return userRep.findAll();
    }

    @Override
    public Optional<User> findUserBy(int id) {
        return userRep.findById(id);
    }
}
