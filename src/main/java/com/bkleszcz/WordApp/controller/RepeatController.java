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
  public ResponseEntity<String> getRandomRepeatedPolishWord(Authentication authentication) {
    return ResponseEntity.ok(repeatService.getRandomRepeatedPolishWord(authentication.getName()));
  }

  @PostMapping("/check")
  public ResponseEntity<Boolean> checkGuess(@RequestBody CheckRequest checkRequest, Authentication authentication) {
    boolean correct = guessingService.checkTranslation(checkRequest.getPolishWord(), checkRequest.getEnglishWord());

    if (authentication != null && authentication.isAuthenticated() &&
            !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
      String loggedUser = authentication.getName(); // login użytkownika
      attemptsService.doAttempt(checkRequest.getPolishWord(), checkRequest.getEnglishWord(), correct);
    }
    return ResponseEntity.ok(correct);
  }

  public static class CheckRequest {

    private String polishWord;
    private String englishWord;

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
