package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.userRepository.UserRepository;
import com.bkleszcz.WordApp.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttemptsServiceTest {

    @Mock
    UserService userService;         // mockujemy zależność
    @InjectMocks
    AttemptsService attemptsService; // SUT – prawdziwy obiekt
    @Mock
    UserRepository userRepository;

    @AfterEach
    void cleanupSecurity() { SecurityContextHolder.clearContext(); }

    @Test
    void getLoggedUserId_returnsId_fromSecurityContext() {
        // given: ustawiamy zalogowanego "Jan" w SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = mock(Authentication.class);
        UserDetails principal = mock(UserDetails.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getUsername()).thenReturn("Jan");
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // oraz rekord użytkownika z repo/serwisu
        User jan = new User();
        jan.setId(42L);
        jan.setUserName("Jan");
        jan.setEmail("jan@ex.com");
        when(userRepository.findByUserName("Jan")).thenReturn(Optional.of(jan));

        // when
        Long id = attemptsService.getLoggedUserId();

        // then
        assertThat(id).isEqualTo(42L);
        verify(userRepository).findByUserName("Jan");
        verifyNoMoreInteractions(userService);
    }


    @Test
    void getLoggedUser() {
//        given
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = mock(Authentication.class);
        UserDetails principal = mock(UserDetails.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getUsername()).thenReturn("Jan");
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // oraz rekord w repozytorium
        User jan = new User();
        jan.setId(42L);
        jan.setUserName("Jan");
        when(userRepository.findByUserName("Jan")).thenReturn(Optional.of(jan));

        // when
        Long id = attemptsService.getLoggedUserId(); // <-- SUT

        // then
        assertThat(id).isEqualTo(42L);
        verify(userRepository).findByUserName("Jan");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getLoggedUserId_returnsZero_whenNoAuth() {
        SecurityContextHolder.clearContext(); // brak auth
        Long id = attemptsService.getLoggedUserId();
        assertThat(id).isEqualTo(0L);
        verifyNoInteractions(userRepository);
    }

    @Test
    void getLoggedUserId_returnsZero_whenUserNotFound() {
        // kontekst z „Ghost”
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        Authentication auth = mock(Authentication.class);
        UserDetails principal = mock(UserDetails.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getUsername()).thenReturn("Ghost");
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        when(userRepository.findByUserName("Ghost")).thenReturn(Optional.empty());

        Long id = attemptsService.getLoggedUserId();
        assertThat(id).isEqualTo(0L);
        verify(userRepository).findByUserName("Ghost");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getDateToday() {
    }

    @Test
    void getAllAttemptsByUserId() {
    }

    @Test
    void getAttemptsDtosByUserId() {
    }

    @Test
    void getYearlyAttemptsDtos() {
    }

    @Test
    void doAttempt() {
    }

    @Test
    void getDailyStats() {
    }

    @Test
    void getSummaryForUser() {
    }

    @Test
    void doAttemptAndBuildResponse() {
    }
}