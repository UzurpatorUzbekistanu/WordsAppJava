package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.model.AppUser;
import com.bkleszcz.WordApp.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Dodanie PasswordEncoder

    // Endpoint do logowania
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AppUser loginRequest, HttpServletRequest request) {
        try {
            // Pobranie użytkownika z bazy danych
            AppUser user = usersService.getUser(loginRequest.getName());

            if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login unsuccessful. Invalid credentials");
                return ResponseEntity.status(403).body(response);  // Zwrócenie błędu, jeśli hasło jest niepoprawne
            }

            // Autentykacja użytkownika (jeśli hasło jest poprawne)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getName(), loginRequest.getPassword())
            );

            // Ustawienie autentykacji w kontekście
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Możesz dodać kod do generowania JWT tokena tutaj, np.:
            // String token = jwtService.generateToken(authentication);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            System.out.println("Session ID: " + request.getSession().getId());

            return ResponseEntity.ok(response);  // Zwróć odpowiedź po udanym logowaniu
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login unsuccessful. Invalid credentials");
            return ResponseEntity.status(403).body(response);  // Błąd, jeśli dane logowania są niepoprawne
        }
    }
}
