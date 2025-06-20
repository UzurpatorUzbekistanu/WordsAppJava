package com.bkleszcz.WordApp.database.userRepository;

import com.bkleszcz.WordApp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    User findByEmail(String email);

    @Override
    void delete(User user);

    @Transactional
    User findByUsername(String username);
}

