package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.domain.User;
import com.bkleszcz.WordApp.service.UserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/UserApi")
public class UserApi {

    private final UserService userService;

    public UserApi(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {

        return userService.createUser(user);
    }
}


