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

  @PostMapping("/checkAuth")                             // endpoint z JWT
  public ResponseEntity<GuessCheckResponse> checkGuessAuth(@RequestBody CheckRequest body                               // payload z frontu
  ) {
    String pl = body.getPolishWord();                              // PL z body
    String en = body.getEnglishWord();                             // EN z body
    boolean rawCorrect = guessingService.isCorrect(pl, en);        // sprawdź poprawność
    boolean penalty = body.isHintPenalty();                        // flaga kary (>15%)

    boolean toPersistAsCorrect = penalty ? false : rawCorrect;     // kara → zapis jako błędne
    GuessCheckResponse dto = attemptsService                       // zapisz + zbuduj DTO
            .doAttemptAndBuildResponse(pl, en, toPersistAsCorrect);    // serwis domenowy

    if (penalty && rawCorrect) {                                   // trafił, ale z karą
      dto.setCorrect(true);                                        // UI: „Correct”
      dto.setExperienceGained(0);                                  // bez XP
      // możesz też dodać dto.setHintPenaltyApplied(true) – jeśli masz to pole
    }
    return ResponseEntity.ok(dto);                                 // 200 OK
  }


  @Getter                                                  // auto-gettery
  public static class CheckRequest {
    private String polishWord;                             // PL
    private String englishWord;                            // EN (wpis usera)
    private boolean hintPenalty;                           // ⬅️ czy przekroczono 15%
  }
}
