package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserApi {

    private final UserService userService;

    public UserApi(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/multi")
    public List<UserResponseDto> getUsersMultiRoles() {
        return userService.getUsersMultiRoles();
    }

    @GetMapping("/pre/{username}")
    public UserResponseDto getUserPreAuth(@PathVariable String username) {
        return userService.getUserPreAuth(username);
    }

    @GetMapping("/secured/{username}")
    public UserResponseDto getUserSecured(@PathVariable String username) {
        return userService.getUserSecured(username);
    }

    @GetMapping("/secured/multi/{username}")
    public UserResponseDto getUserSecuredMultiRoles(@PathVariable String username) {
        return userService.getUserSecuredMultiRoles(username);
    }

    @GetMapping("/jsr250/{username}")
    public UserResponseDto getUserJsr250(@PathVariable String username) {
        return userService.getUserJsr250(username);
    }

    @GetMapping("/jsr250/multi/{username}")
    public UserResponseDto getUserJsr250MultiRoles(@PathVariable String username) {
        return userService.getUserJsr250MultiRoles(username);
    }
}


