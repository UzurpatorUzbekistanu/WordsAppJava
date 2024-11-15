package com.bkleszcz.WordApp.database;

import com.bkleszcz.WordApp.model.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <AppUser, Long> {

  Optional<AppUser> findByName(String name);
}
