package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.AttemptsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishWordRepository;
import com.bkleszcz.WordApp.database.UserRepository;
import com.bkleszcz.WordApp.model.Attempts;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AttemptsService {

  private final AttemptsRepository attemptsRepository;
  private final PolishWordRepository polishWordRepository;
  private final EnglishWordRepository englishWordRepository;
  private final UserRepository userRepository;

  public AttemptsService(AttemptsRepository attemptsRepository,
                         PolishWordRepository polishWordRepository,
                         EnglishWordRepository englishWordRepository,
                         UserRepository userRepository) {
    this.attemptsRepository = attemptsRepository;
    this.polishWordRepository = polishWordRepository;
    this.englishWordRepository = englishWordRepository;
    this.userRepository = userRepository;
  }

  public void doAttempt(String polishWord, String englishWord, String loggedUser, boolean isCorrect) {
    long polishWordId = polishWordRepository.findByWord(polishWord).get().getId();
    Long userId = userRepository.findByName(loggedUser).map(user -> user.getId()).orElse(null);
    long englishWordId = englishWordRepository.findByWord(englishWord).get().getId();

    LocalDate localDate = LocalDate.now();
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

//    anonymous session
    if (userId == null) {
      return;
    }
    Optional<Attempts> attempt = attemptsRepository.findByPolishWord_IdAndAppUser_Id(polishWordId, userId);

    if (attempt.isPresent()) {
      updateAttempt(attempt.get(), date, isCorrect);
    }
    else {
      createAttempt(polishWordId, englishWordId, userId, date, isCorrect);
    }
  }

  private void updateAttempt(Attempts attempt, Date date, boolean isCorrect) {
    attempt.setDateLastTry(date);
    attempt.setNumberOfAttempts(attempt.getNumberOfAttempts() + 1);
    if (isCorrect){
      attempt.setDateLastSuccess(date);
      attempt.setCorrectAnswers(attempt.getCorrectAnswers() + 1);
      attempt.setLevel(attempt.getLevel() + 1);
    }
    else {
      attempt.setWrongAnswers(attempt.getWrongAnswers() + 1);
      if (attempt.getLevel() > 0){
        attempt.setLevel(attempt.getLevel() - 1);
      }
    }

    attempt.setDateRepeat(generateDateRepeat(attempt.getLevel()));

    attemptsRepository.save(attempt);
  }

  private void createAttempt(long polishWordId, long englishWordId, long userId, Date date, boolean isCorrect){

    Attempts newAttempt = Attempts.builder()
        .polishWord(polishWordRepository.findById(polishWordId).get())
        .englishWord(englishWordRepository.findById(englishWordId).get())
        .appUser(userRepository.findById(userId).get())
        .dateLastTry(date)
        .dateLastSuccess(isCorrect ? date : null)
        .numberOfAttempts(1)
        .correctAnswers(isCorrect ? 1 : 0)
        .wrongAnswers(isCorrect ? 0 : 1)
        .level(isCorrect ? 1 : 0)
        .dateRepeat(generateDateRepeat(1))
        .build();

    attemptsRepository.save(newAttempt);

  }

  private Date generateDateRepeat(int level) {
    LocalDate localDate = LocalDate.now();
    LocalDate localDateRepeat = null;

    switch(level){
      case 0:
      case 1:
        localDateRepeat =  localDate.plusDays(1);
        break;
      case 2:
        localDateRepeat = localDate.plusDays(3);
        break;
      case 3:
        localDateRepeat =  localDate.plusDays(7);
        break;
      case 4:
        localDateRepeat = localDate.plusDays(21);
        break;
      case 5:
        localDateRepeat =  localDate.plusDays(48);
        break;
      default:
        if (level > 5) {
          localDateRepeat = localDate.plusDays(120);
        }
        break;
    }
    Date date = Date.from(localDateRepeat.atStartOfDay(ZoneId.systemDefault()).toInstant());

    return date;
  }

}
