package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.userRepository.UserRepository;
import com.bkleszcz.WordApp.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    return userRepository.findByUserName(userName)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with this name: " + userName));
  }

  List<User> top3StrikesUsers () {
    return userRepository.findAllByOrderByStrikeBestDesc(PageRequest.of(0,3));
  }

  List<User> top3ScoreUsers () {
    return userRepository.findAllByOrderByExperienceDesc(PageRequest.of(0,3));
  }


    public Optional<User> findByUserName(String username) {
      return userRepository.findByUserName(Objects.requireNonNull(getLoggedUser()).getUsername());
    }

  private User getLoggedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String)) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        String username = ((UserDetails) principal).getUsername();
        return userRepository.findByUserName(username)
                .orElse(null);
      }
    }
    return null;
  }

}
