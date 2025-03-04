package com.bkleszcz.WordApp.controller;


import com.bkleszcz.WordApp.model.AppUser;
import com.bkleszcz.WordApp.service.UsersService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UsersController {

  private final UsersService usersService;

  @Autowired
  public UsersController(UsersService usersService) {
    this.usersService = usersService;
  }

  @GetMapping("/all")
  public List<String> getAllUsers() {
    return usersService.getAllUsers();
  }

  @PostMapping("/save")
  public ResponseEntity<Map<String, String>> createUser(@RequestBody AppUser appUser) {
    try{
      usersService.createUser(appUser);

      Map<String, String> response = new HashMap<>();
      response.put("message", "User saved successfully");

      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      Map<String, String> response = new HashMap<>();
      response.put("message", "User not saved");

      return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

  }

  @GetMapping("/user")
  public AppUser getUserInfo() {
    // Pobierz aktualnie zalogowanego użytkownika
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    System.out.println(authentication);
    // Jeśli użytkownik jest zalogowany
    if (authentication != null && authentication.isAuthenticated()) {
      String username = authentication.getName();
      return usersService.getUser(username);
    }

    throw new RuntimeException("User not authenticated");
  }

}
