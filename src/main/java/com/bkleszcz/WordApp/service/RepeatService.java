package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.AttemptsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishWordRepository;
import com.bkleszcz.WordApp.database.UserRepository;
import com.bkleszcz.WordApp.model.AppUser;
import com.bkleszcz.WordApp.model.Attempts;
import com.bkleszcz.WordApp.model.PolishWord;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    String loggedInUserName = getLoggedUsername();

    if (loggedInUserName != null) {
      AppUser user = userRepository.findByName(loggedInUserName).get();
      List<Attempts> attemptsToRepeat = attemptsRepository.findByAppUser_IdAndDateRepeatLessThanEqual(user.getId(), date );
      if (attemptsToRepeat.size() == 0){
        return "nie masz powtórek na dziś";
      }
      int randomIndex = new Random().nextInt(attemptsToRepeat.size());
      return attemptsToRepeat.get(randomIndex).getPolishWord().getWord();
    }
    return "jesteś niezalogowany";

    }


  public String getLoggedUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Sprawdzenie, czy kontekst uwierzytelniania jest poprawny i zalogowany użytkownik nie jest anonimowy
    if (authentication != null && authentication.isAuthenticated()
        && !(authentication.getPrincipal() instanceof String)) {

      Object principal = authentication.getPrincipal();

      if (principal instanceof UserDetails) {
        return ((UserDetails) principal).getUsername(); // Zwraca nazwę użytkownika
      }
    }
    return null;
  }
}
