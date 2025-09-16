package com.bkleszcz.WordApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean                                          // udostępnij encoder
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();          // BCrypt
    }
}
