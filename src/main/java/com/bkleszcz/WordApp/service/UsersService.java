package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.UserRepository;
import com.bkleszcz.WordApp.model.AppUser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

  private final UserRepository usersRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UsersService(UserRepository userRepository, PasswordEncoder passwordEncoder){
    this.usersRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<String> getAllUsers(){

    List<AppUser> appUsers = usersRepository.findAll();

    List<String> usersName = new ArrayList<String>();

    appUsers.forEach(appUser -> usersName.add(appUser.getName()));

    return usersName;
  }

  public AppUser createUser(AppUser appUser){
    if (usersRepository.findByName(appUser.getName()).isPresent()){
      throw new IllegalArgumentException("User with this name already exists");
    }
    appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
    return usersRepository.save(appUser);
  }

    public AppUser getUser(String name){
        return usersRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
