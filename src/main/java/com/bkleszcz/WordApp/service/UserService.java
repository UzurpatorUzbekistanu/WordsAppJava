package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.controller.UserResponseDto;
import com.bkleszcz.WordApp.database.userRepository.UserRepository;
import com.bkleszcz.WordApp.domain.User;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PreAuthorize("hasRole('ROLE_API_READ_PRIVILEGE')")
  public List<UserResponseDto> getUsers() {
    return userRepository.findAll().stream().map(User::toDto).collect(Collectors.toList());
  }

  @PreAuthorize("hasRole('ROLE_API_READ_PRIVILEGE') or hasRole('ROLE_ADMIN')")
  public List<UserResponseDto> getUsersMultiRoles() {
    return userRepository.findAll().stream().map(User::toDto).collect(Collectors.toList());
  }

  @PostAuthorize("#username == authentication.principal.email")
  public UserResponseDto getUserPreAuth(String username) {
    return userRepository.findByEmail(username).toDto();
  }

  @Secured("ROLE_API_READ_PRIVILEGE")
  public UserResponseDto getUserSecured(String username) {
    return userRepository.findByEmail(username).toDto();
  }

  @Secured({ "ROLE_API_READ_PRIVILEGE", "ROLE_ADMIN" })
  public UserResponseDto getUserSecuredMultiRoles(String username) {
    return userRepository.findByEmail(username).toDto();
  }

  @RolesAllowed("ROLE_API_READ_PRIVILEGE")
  public UserResponseDto getUserJsr250(String username) {
    return userRepository.findByEmail(username).toDto();
  }

  @RolesAllowed({ "ROLE_API_READ_PRIVILEGE", "ROLE_ADMIN" })
  public UserResponseDto getUserJsr250MultiRoles(String username) {
    return userRepository.findByEmail(username).toDto();
  }
}
