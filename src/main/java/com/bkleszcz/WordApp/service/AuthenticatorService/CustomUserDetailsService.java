package com.bkleszcz.WordApp.service.AuthenticatorService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.bkleszcz.WordApp.database.userRepository.UserRepository;
import com.bkleszcz.WordApp.domain.Privilege;
import com.bkleszcz.WordApp.domain.Role;
import com.bkleszcz.WordApp.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

    try {
      final User user = userRepository.findByEmail(email);
      if (user == null) {
        throw new UsernameNotFoundException("No user found with username: " + email);
      }

      return new org.springframework.security
              .core.userdetails.User(user.getEmail(),
              user.getPassword(),
              user.isEnabled(),
              true,
              true,
              true,
              getAuthorities(user.getRoles()));
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
    List<String> authorities = getPrivileges(roles);
    authorities.addAll(roles.stream().map(Role::getName).collect(Collectors.toList()));
    return getGrantedAuthorities(authorities);
  }

  private List<String> getPrivileges(final Collection<Role> roles) {
    final List<String> privileges = new ArrayList<>();
    final List<Privilege> collection = new ArrayList<>();
    for (final Role role : roles) {
      collection.addAll(role.getPrivileges());
    }
    for (final Privilege item : collection) {
      privileges.add(item.getName());
    }

    return privileges;
  }

  private List<GrantedAuthority> getGrantedAuthorities(final List<String> authorities) {
    final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (final String authority : authorities) {
      grantedAuthorities.add(new SimpleGrantedAuthority(authority));
    }
    return grantedAuthorities;
  }
}