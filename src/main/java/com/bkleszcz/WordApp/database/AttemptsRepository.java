package com.bkleszcz.WordApp.database;


import com.bkleszcz.WordApp.model.Attempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptsRepository extends JpaRepository<Attempts, Long> {
}
