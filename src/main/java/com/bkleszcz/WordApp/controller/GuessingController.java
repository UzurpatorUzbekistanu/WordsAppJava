package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.service.GuessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guess")
public class GuessingController {

  private final GuessingService guessingService;

  @Autowired
  public GuessingController(GuessingService guessingService) {
    this.guessingService = guessingService;
  }

  @GetMapping("/random")
  public ResponseEntity<String> getRandomPolishWord() {
    return ResponseEntity.ok(guessingService.getRandomPolishWord());
  }

  // Zmiana: używamy @RequestBody, aby pobrać dane z ciała żądania
  @PostMapping("/check")
  public ResponseEntity<Boolean> checkGuess(@RequestBody CheckRequest checkRequest) {
    boolean correct = guessingService.checkTranslation(checkRequest.getPolishWord(), checkRequest.getEnglishWord());
    return ResponseEntity.ok(correct);
  }

  // Klasa pomocnicza do mapowania danych z ciała żądania
  public static class CheckRequest {
    private String polishWord;
    private String englishWord;

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
  }
}
