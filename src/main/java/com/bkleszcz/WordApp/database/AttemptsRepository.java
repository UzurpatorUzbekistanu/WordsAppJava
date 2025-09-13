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

  List<Attempts> findByAppUser_IdAndDateRepeatLessThanEqualAndIsRepeatedFalse(Long userId, Date date);

  List<Attempts> findByAppUserId(Long userId);

  List<Attempts> findByDateLastTryAfterAndIsCorrectAnswerTrue(Date dayYearAgo);

  int countByPolishWordIdAndEnglishWordIdAndAppUser_Id(int polishWordId, int englishWordId, Long userId);

  Optional<Attempts> findFirstByPolishWordIdAndEnglishWordIdAndAppUser_IdOrderByDateLastTryDesc(
          int polishWordId, int englishWordId, long userId);


}
