package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.model.dto.GuessCheckResponse;
import com.bkleszcz.WordApp.service.AttemptsService;
import com.bkleszcz.WordApp.service.GuessingService;
import com.bkleszcz.WordApp.service.SynonymService;
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
  public ResponseEntity<GuessCheckResponse> checkGuess(@RequestBody CheckRequest body) {
    final String pl = body.getPolishWord();
    final String en = body.getEnglishWord();

    GuessCheckResponse dto = guessingService.buildAndCheckResponseForUnauthenticatedUser(pl, en);

    return ResponseEntity.ok(dto);
  }

  @PostMapping("/checkAuth")
  public ResponseEntity<GuessCheckResponse> checkGuessAuth(@RequestBody CheckRequest body
  ) {
    String pl = body.getPolishWord();
    String en = body.getEnglishWord();
    boolean rawCorrect = guessingService.checkTranslation(pl, en);
    boolean penalty = body.isHintPenalty();

    boolean toPersistAsCorrect = penalty ? false : rawCorrect;
    GuessCheckResponse dto = attemptsService
            .doAttemptAndBuildResponse(pl, en, toPersistAsCorrect);

    if (penalty && rawCorrect) {
      dto.setCorrect(true);
      dto.setExperienceGained(0);
    }
    return ResponseEntity.ok(dto);
  }


  @Getter
  public static class CheckRequest {
    private String polishWord;
    private String englishWord;
    private boolean hintPenalty;
  }
}
