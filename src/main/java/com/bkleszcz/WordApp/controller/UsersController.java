package com.bkleszcz.WordApp.controller;


import com.bkleszcz.WordApp.model.AppUser;
import com.bkleszcz.WordApp.service.UsersService;
import java.util.List;
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
  public ResponseEntity<String> createUser(@RequestBody AppUser appUser) {
    try{
      usersService.createUser(appUser);
      return ResponseEntity.ok("User saved successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

  }

  @GetMapping("/user")
  public String getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return authentication.getName();  // Zwraca nazwę zalogowanego użytkownika
    } else {
      return "Gość";  // Jeśli użytkownik nie jest zalogowany
    }
  }


}
