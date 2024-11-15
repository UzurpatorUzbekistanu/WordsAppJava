package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.UserRepository;
import com.bkleszcz.WordApp.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser appUser = userRepository.findByName(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return User.builder()
        .username(appUser.getName())
        .password(appUser.getPassword())
        .roles("USER")
        .build();
  }
}