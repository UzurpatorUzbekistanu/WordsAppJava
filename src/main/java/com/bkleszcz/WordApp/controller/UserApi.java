package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.domain.User;
import com.bkleszcz.WordApp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/loggedUser")
    public ResponseEntity<String> whichUserAreLogged(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String )) {
            return ResponseEntity.ok(authentication.getName());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

}


