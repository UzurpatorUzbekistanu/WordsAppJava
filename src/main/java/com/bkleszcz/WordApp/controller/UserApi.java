package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.domain.User;
import com.bkleszcz.WordApp.model.dto.LevelInfoDto;
import com.bkleszcz.WordApp.model.dto.UserInfoDto;
import com.bkleszcz.WordApp.service.ExperienceService;
import com.bkleszcz.WordApp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/UserApi")
public class UserApi {

    private final UserService userService;
    private final ExperienceService experienceService;

    public UserApi(UserService userService, ExperienceService experienceService) {
        this.userService = userService;
        this.experienceService = experienceService;
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {

        return userService.createUser(user);
    }

    @GetMapping("/loggedUser")                        // GET /UserApi/loggedUser
    public ResponseEntity<UserInfoDto> whichUserAreLogged(Authentication auth) { // wstrzyknięte auth
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) { // gdy zalogowany
            String username = auth.getName();             // weź nazwę
            Optional<User> user = userService.findByUserName(username); // znajdź encję po nazwie
            return ResponseEntity.ok(new UserInfoDto(user.get().getId(), user.get().getUsername())); // zwróć id + username
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // brak auth → 401
    }

    @GetMapping("/me/level")                               // ⬅️ NOWY ENDPOINT
    public ResponseEntity<LevelInfoDto> myLevel(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {       // musi być JWT
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<User> u = userService.findByUserName(auth.getName()); // pobierz usera
        if (u.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        int before = u.get().getLevel().getNumber();         // numer przed (dla flagi)
        LevelInfoDto dto = experienceService.buildLevelInfo(u.get(), before); // policz stan
        dto.setLevelUp(false);                                // GET nie odpala pop-upu
        return ResponseEntity.ok(dto);                        // 200 + JSON
    }
}




