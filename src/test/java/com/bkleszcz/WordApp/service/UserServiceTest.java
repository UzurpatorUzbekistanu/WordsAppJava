package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.userRepository.UserRepository;
import com.bkleszcz.WordApp.domain.Strike;
import com.bkleszcz.WordApp.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void createUser_hashesPassword_andSaves() {
        // given
        User req = new User();
        req.setUserName("john");
        req.setEmail("john@ex.com");
        req.setPassword("pw");

        when(passwordEncoder.encode("pw")).thenReturn("enc");

        // symulujemy nadanie ID przez repo (jak baza przy INSERT)
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0, User.class);
            User saved = new User();
            saved.setId(1L);
            saved.setUserName(u.getUsername());
            saved.setEmail(u.getEmail());
            saved.setPassword(u.getPassword());
            return saved;
        });

        // when
        User created = userService.createUser(req);

        // then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getPassword()).isEqualTo("enc");

        verify(passwordEncoder).encode("pw");
        verify(userRepository, times(1)).save(argThat(userWith("john", "john@ex.com", "enc")));
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    private static ArgumentMatcher<User> userWith(String userName, String email, String encodedPw) {
        return u -> u != null
                && userName.equals(u.getUsername())
                && email.equals(u.getEmail())
                && encodedPw.equals(u.getPassword());
    }

    @Test
    void getAllUsers() {
        var user1 = user("john", "a@a.pl", "pw", 1L);
        var user2 = user("matylda", "b@b.pl", "pww", 2L);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

//        when
        var result = userService.getAllUsers();

//        then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getId).containsExactly(1L, 2L);
        assertThat(result).extracting(User::getUsername).containsExactly("john","matylda");
    }

    private static User user(String name, String email, String pw, Long id) {
        var u = new User();
        u.setUserName(name);
        u.setEmail(email);
        u.setPassword(pw);
        u.setId(id);
        return u;
    }

    @Test
    void loadUserByUsername() {
//        given
        var user1 = user("Jan", "a@a.pl", "pw", 1L);
        when(userRepository.findByUserName("Jan")).thenReturn(Optional.of(user1));
// when
        UserDetails details = userService.loadUserByUsername("Jan");

        // then (asercje przez interfejs UserDetails)
        assertThat(details).isNotNull();
        assertThat(details.getUsername()).isEqualTo("Jan");
        assertThat(details.getPassword()).isEqualTo("pw");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities()).isEmpty(); // u Ciebie zwraca pustą listę

        // dodatkowo możesz sprawdzić pole spoza interfejsu – po rzutowaniu:
        assertThat(details).isInstanceOf(User.class);
        assertThat(((User) details).getEmail()).isEqualTo("a@a.pl");
        assertThat(((User) details).getId()).isEqualTo(1L);

    }

    @Test
    void top3StrikesUsers() {
        // given
        var u1 = user("Jan",   "a@a.pl",  "abc", 100L);
        var u2 = user( "Anna",  "b@b.pl",   "abc", 90L);
        var u3 = user( "Marek", "c@c.pl",   "abc", 80L);
        u1.setStrikeCurrent(Strike.FIVE);
        u2.setStrikeCurrent(Strike.FIVE);
        u3.setStrikeCurrent(Strike.FIVE);
        when(userRepository.findAllByOrderByStrikeBestDesc(PageRequest.of(0, 3)))
                .thenReturn(List.of(u1, u2, u3));

        // when
        var result = userService.top3StrikesUsers();

        // then
        assertThat(result).containsExactly(u1, u2, u3);
        verify(userRepository).findAllByOrderByStrikeBestDesc(PageRequest.of(0, 3));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void top3ScoreUsers() {
        // given
        var u1 = user("Jan",   "a@a.pl",  "abc", 100L);
        var u2 = user( "Anna",  "b@b.pl",   "abc", 90L);
        var u3 = user( "Marek", "c@c.pl",   "abc", 80L);
        u1.setExperience(100);
        u2.setExperience(100);
        u3.setExperience(100);
        when(userRepository.findAllByOrderByExperienceDesc(PageRequest.of(0, 3)))
                .thenReturn(List.of(u1, u2, u3));

        // when
        var result = userService.top3ScoreUsers();

        // then
        assertThat(result).containsExactly(u1, u2, u3);
        verify(userRepository).findAllByOrderByExperienceDesc(PageRequest.of(0, 3));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByUserName_readsFromSecurityContext_andQueriesRepository() {
        // given: zalogowany użytkownik "Jan" w SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = mock(Authentication.class);
        UserDetails principal = mock(UserDetails.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getUsername()).thenReturn("Jan");
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // oraz rekord w repo
        var jan = user("Jan",   "a@a.pl",  "abc", 1L);
        when(userRepository.findByUserName("Jan")).thenReturn(Optional.of(jan));

        // when: UWAGA — serwis ignoruje parametr i patrzy w kontekst
        var result = userService.findByUserName("IGNORED");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("Jan");
        assertThat(result.get().getId()).isEqualTo(1L);

        verify(userRepository, times(2)).findByUserName("Jan");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }
}