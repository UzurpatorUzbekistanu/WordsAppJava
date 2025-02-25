package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.service.AttemptsService;
import com.bkleszcz.WordApp.service.GuessingService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guess")
public class GuessingController {

  private final GuessingService guessingService;
  private final AttemptsService attemptsService;

  @Autowired
  public GuessingController(GuessingService guessingService, AttemptsService attemptsService) {
    this.guessingService = guessingService;
    this.attemptsService = attemptsService;
  }

  @GetMapping("/random")
  public ResponseEntity<String> getRandomPolishWord() {
    return ResponseEntity.ok(guessingService.getRandomPolishWord());
  }

  // Zmiana: używamy @RequestBody, aby pobrać dane z ciała żądania
  @PostMapping("/check")
  public ResponseEntity<Boolean> checkGuess(@RequestBody CheckRequest checkRequest) {
    boolean correct = guessingService.checkTranslation(checkRequest.getPolishWord(), checkRequest.getEnglishWord());

    System.out.println("logged user: " + checkRequest.getLoggedUser());
    if(!checkRequest.getLoggedUser().equals("anonymousUser")) {
      attemptsService.doAttempt(checkRequest.getPolishWord(), checkRequest.getEnglishWord(), checkRequest.getLoggedUser(), correct);
    }
    return ResponseEntity.ok(correct);
  }

  @Getter
  public static class CheckRequest {
      // Gettery i settery
      private String polishWord;
    private String englishWord;
    private String loggedUser;

      public void setPolishWord(String polishWord) {
      this.polishWord = polishWord;
    }

      public void setEnglishWord(String englishWord) {
      this.englishWord = englishWord;
    }

      public void setLoggedUser(String loggedUser) {
      this.loggedUser = loggedUser;
    }
  }
}
