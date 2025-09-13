package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.model.dto.GuessCheckResponse;
import com.bkleszcz.WordApp.service.AttemptsService;
import com.bkleszcz.WordApp.service.GuessingService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
  public ResponseEntity<List<String>> getRandomPolishWord() {
    List<String> words = new ArrayList<>();
    words.add(guessingService.getRandomPolishWord());
    return ResponseEntity.ok(words);
  }

  @GetMapping("/randomAuth")
  public ResponseEntity<List<String>> getRandomPolishWordAuth() {
    List<String> words = new ArrayList<>();
    words.add(guessingService.getRandomPolishWord());
    return ResponseEntity.ok(words);
  }

  @PostMapping("/check")
  public ResponseEntity<Boolean> checkGuess(@RequestBody CheckRequest checkRequest) {
    boolean correct = guessingService.checkTranslation(checkRequest.getPolishWord(), checkRequest.getEnglishWord());
    return ResponseEntity.ok(correct);
  }

  @PostMapping("/checkAuth")
  public ResponseEntity<GuessCheckResponse> checkGuessAuth(@RequestBody CheckRequest checkRequest, Authentication authentication) {
    boolean correct = guessingService.checkTranslation(checkRequest.getPolishWord(), checkRequest.getEnglishWord());

    AttemptsService.AttemptResult result = attemptsService.doAttempt(checkRequest.getPolishWord(), checkRequest.getEnglishWord(), correct);

    GuessCheckResponse response = new GuessCheckResponse(correct, result.getExperienceGained(), result.getCurrentStrike());

    return ResponseEntity.ok(response);
  }


  @Getter
  public static class CheckRequest {
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
