package com.bkleszcz.WordApp.database.userRepository;

import com.bkleszcz.WordApp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    Optional<User> findById(Long id);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);

    List<User> findAllByOrderByStrikeBestDesc(Pageable pageable);

    List<User> findAllByOrderByExperienceDesc(Pageable pageable);

}