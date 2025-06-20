package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.AttemptsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishWordRepository;
import com.bkleszcz.WordApp.database.userRepository.UserRepository;
import com.bkleszcz.WordApp.domain.User;
import com.bkleszcz.WordApp.model.Attempts;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class RepeatService {

  private final AttemptsRepository attemptsRepository;
  private final PolishWordRepository polishWordRepository;
  private final EnglishWordRepository englishWordRepository;
  private final UserRepository userRepository;


  public RepeatService(AttemptsRepository attemptsRepository, PolishWordRepository polishWordRepository,
                       EnglishWordRepository englishWordRepository, UserRepository userRepository) {
    this.attemptsRepository = attemptsRepository;
    this.polishWordRepository = polishWordRepository;
    this.englishWordRepository = englishWordRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/random")
  public String getRandomRepeatedPolishWord(String userName) {

    LocalDate localDate = LocalDate.now();
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    User user = userRepository.findByUsername(userName);
    if (user == null){
      return "Jesteś niezalogowany";
    }

    List<Attempts> attemptsToRepeat = attemptsRepository.findByAppUser_IdAndDateRepeatLessThanEqual(user.getId(), date );

    if (attemptsToRepeat.size() == 0){
      return "nie masz powtórek na dziś";
    }
    int randomIndex = new Random().nextInt(attemptsToRepeat.size());
    return attemptsToRepeat.get(randomIndex).getPolishWord().getWord();

  }
}
