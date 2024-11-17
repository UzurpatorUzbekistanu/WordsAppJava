package com.bkleszcz.WordApp.database;


import com.bkleszcz.WordApp.model.Attempts;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptsRepository extends JpaRepository<Attempts, Long> {

  Optional<Attempts> findByPolishWord_IdAndAppUser_Id(Long polishWordId, Long userId);

  List<Attempts> findByAppUser_IdAndDateRepeatLessThanEqual(Long userId, Date date);

}
