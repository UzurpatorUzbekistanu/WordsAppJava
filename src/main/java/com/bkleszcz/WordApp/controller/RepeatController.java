package com.bkleszcz.WordApp.controller;


import com.bkleszcz.WordApp.service.AttemptsService;
import com.bkleszcz.WordApp.service.GuessingService;
import com.bkleszcz.WordApp.service.RepeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/repeats")
public class RepeatController {

  private final RepeatService repeatService;
  private final GuessingService guessingService;
  private final AttemptsService attemptsService;

  public RepeatController(RepeatService repeatService, GuessingService guessingService, AttemptsService attemptsService) {
    this.repeatService = repeatService;
    this.guessingService = guessingService;
    this.attemptsService = attemptsService;
  }

  @GetMapping("/random")
  public ResponseEntity<String> getRandomRepeatedPolishWord(@RequestParam String username) {
    return ResponseEntity.ok(repeatService.getRandomRepeatedPolishWord(username));
  }

  @PostMapping("/check")
  public ResponseEntity<Boolean> checkGuess(@RequestBody CheckRequest checkRequest) {
    boolean correct = guessingService.checkTranslation(checkRequest.getPolishWord(), checkRequest.getEnglishWord());

    attemptsService.doAttempt(checkRequest.getPolishWord(), checkRequest.getEnglishWord(), getLoggedUsername(), correct);

    return ResponseEntity.ok(correct);
  }

  // Klasa pomocnicza do mapowania danych z ciała żądania
  public static class CheckRequest {

    private String polishWord;
    private String englishWord;
    private String loggedUser;

    // Gettery i settery
    public String getPolishWord() {
      return polishWord;
    }

    public void setPolishWord(String polishWord) {
      this.polishWord = polishWord;
    }

    public String getEnglishWord() {
      return englishWord;
    }

    public void setEnglishWord(String englishWord) {
      this.englishWord = englishWord;
    }
    public String getLoggedUser(){
      return loggedUser;
    }
    public void setLoggedUser(String loggedUser) {
      this.loggedUser = loggedUser;
    }
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
