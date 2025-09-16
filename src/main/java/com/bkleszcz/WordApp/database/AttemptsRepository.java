package com.bkleszcz.WordApp.database;


import com.bkleszcz.WordApp.database.projection.UserScoreView;
import com.bkleszcz.WordApp.model.Attempts;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  // top all-time
  @Query("""
    select u.id, u.userName, coalesce(sum(a.experienceGained),0)
    from Attempts a join a.appUser u
    group by u.id, u.userName
    order by coalesce(sum(a.experienceGained),0) desc
  """)
  List<Object[]> sumAllXpByUser();

  // dowolny zakres [start, end)
  @Query("""
    select u.id, u.userName, coalesce(sum(a.experienceGained),0)
    from Attempts a join a.appUser u
    where a.dateLastTry >= :start and a.dateLastTry < :end
    group by u.id, u.userName
    order by coalesce(sum(a.experienceGained),0) desc
  """)
  List<Object[]> sumXpByUserBetween(@Param("start") Date start,
                                    @Param("end")   Date end);

}
