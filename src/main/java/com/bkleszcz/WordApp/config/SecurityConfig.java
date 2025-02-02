package com.bkleszcz.WordApp.config;

import com.bkleszcz.WordApp.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;

  public SecurityConfig(CustomUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/auth/login")
            .ignoringRequestMatchers("/api/guess/check")
            .ignoringRequestMatchers("/api/users/save")
            .ignoringRequestMatchers("/api/repeats/random")
            .ignoringRequestMatchers("/api/repeats/check")
            .ignoringRequestMatchers("/synonyms")
        )
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()  // Zezwala na dostęp do wszystkich zasobów, niezależnie od logowania
        )
        .formLogin(form -> form
            .loginPage("/users")  // Określa stronę logowania, która wyświetla formularz logowania
            .loginProcessingUrl("/auth/login")  // Zmieniamy URL, gdzie formularz jest przetwarzany
            .defaultSuccessUrl("/", true)  // Po zalogowaniu, użytkownik zostanie przekierowany na /index
            .permitAll()  // Zezwala na dostęp do strony logowania
        )
        .logout(logout -> logout
            .permitAll()  // Zezwala na dostęp do logout
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }
}

