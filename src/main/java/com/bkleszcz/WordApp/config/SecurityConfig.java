package com.bkleszcz.WordApp.config; // Deklaracja pakietu

import com.bkleszcz.WordApp.service.AuthenticatorService.CustomAuthFailureHandler; // Import niestandardowego handlera niepowodzenia logowania
import com.bkleszcz.WordApp.service.AuthenticatorService.CustomAuthSuccessHandler; // Import niestandardowego handlera sukcesu logowania
import com.bkleszcz.WordApp.service.AuthenticatorService.CustomAuthenticationProvider; // Import niestandardowego providera autentykacji
import com.bkleszcz.WordApp.service.AuthenticatorService.CustomLogoutSuccessHandler; // Import niestandardowego handlera sukcesu wylogowania
import org.springframework.context.annotation.Bean; // Import adnotacji @Bean
import org.springframework.context.annotation.Configuration; // Import adnotacji @Configuration
import org.springframework.security.authentication.AuthenticationManager; // Import interfejsu AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Import klasy DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Włącza zabezpieczenia metod (zamiast EnableGlobalMethodSecurity)
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Włącza zabezpieczenia webowe
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Import klasy do budowania konfiguracji HTTP
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // Import do pomijania zabezpieczeń dla wybranych ścieżek
import org.springframework.security.core.userdetails.UserDetailsService; // Import interfejsu UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import klasy BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder; // Import interfejsu PasswordEncoder
import org.springframework.security.web.SecurityFilterChain; // Import SecurityFilterChain

@Configuration // Oznacza klasę jako źródło konfiguracji Spring
@EnableWebSecurity // Włącza mechanizmy zabezpieczeń webowych
@EnableMethodSecurity // Włącza zabezpieczenia na poziomie metod
public class SecurityConfig { // Deklaracja klasy konfiguracyjnej

  // Definicja pól zależności
  private final CustomAuthSuccessHandler customAuthSuccessHandler; // Obsługa sukcesu logowania
  private final CustomAuthFailureHandler customAuthFailureHandler; // Obsługa błędu logowania
  private final CustomLogoutSuccessHandler customLogoutSuccessHandler; // Obsługa sukcesu wylogowania
  private final UserDetailsService userDetailsService; // Serwis do pobierania danych użytkownika

  // Konstruktor wstrzykujący zależności
  public SecurityConfig(CustomAuthSuccessHandler customAuthSuccessHandler,
                        CustomAuthFailureHandler customAuthFailureHandler,
                        CustomLogoutSuccessHandler customLogoutSuccessHandler,
                        UserDetailsService userDetailsService) {
    this.customAuthSuccessHandler = customAuthSuccessHandler; // Przypisanie handlera sukcesu logowania
    this.customAuthFailureHandler = customAuthFailureHandler; // Przypisanie handlera błędu logowania
    this.customLogoutSuccessHandler = customLogoutSuccessHandler; // Przypisanie handlera sukcesu wylogowania
    this.userDetailsService = userDetailsService; // Przypisanie serwisu użytkowników
  }

  @Bean // Definiuje bean typu SecurityFilterChain
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable()) // Wyłącza ochronę CSRF (można zmodyfikować według potrzeb)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/home", "/login*", "/console/**").permitAll() // Umożliwia dostęp do wymienionych ścieżek bez uwierzytelnienia
                    .anyRequest().authenticated() // Wymaga uwierzytelnienia dla pozostałych żądań
            )
            .formLogin(form -> form
                    .loginPage("/login") // Ustawia niestandardową stronę logowania
                    .failureUrl("/login?error=true") // Przekierowanie przy błędnym logowaniu
                    .successHandler(customAuthSuccessHandler) // Ustawia niestandardowy handler sukcesu logowania
                    .failureHandler(customAuthFailureHandler) // Ustawia niestandardowy handler błędu logowania
            )
            .logout(logout -> logout
                    .logoutSuccessHandler(customLogoutSuccessHandler) // Ustawia niestandardowy handler sukcesu wylogowania
                    .invalidateHttpSession(false) // Nie unieważnia sesji HTTP przy wylogowaniu
                    .deleteCookies("JSESSIONID") // Usuwa ciasteczko JSESSIONID przy wylogowaniu
            )
            .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin()) // Pozwala na wyświetlanie ramek tylko z tej samej domeny
            );
    return http.build(); // Buduje i zwraca obiekt SecurityFilterChain
  }

  @Bean // Definiuje bean typu AuthenticationManager
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManager.class); // Pobiera wspólny obiekt AuthenticationManager
  }

  @Bean // Definiuje bean dla niestandardowego providera autentykacji
  public DaoAuthenticationProvider authProvider() {
    CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider(); // Tworzy instancję niestandardowego providera
    authProvider.setUserDetailsService(userDetailsService); // Ustawia serwis użytkowników
    authProvider.setPasswordEncoder(passwordEncoder()); // Ustawia enkoder haseł
    return authProvider; // Zwraca skonfigurowanego providera
  }

  @Bean // Definiuje bean typu PasswordEncoder
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // Zwraca instancję BCrypt do enkodowania haseł
  }

  @Bean // Definiuje bean pozwalający na pomijanie zabezpieczeń dla zasobów statycznych
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().requestMatchers("/resources/**"); // Ignoruje zabezpieczenia dla ścieżek zaczynających się od /resources/
  }
}
