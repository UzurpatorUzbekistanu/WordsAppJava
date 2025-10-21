package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.config.ApiUsageCounterScheduler;
import com.bkleszcz.WordApp.service.GuessingService;
import com.bkleszcz.WordApp.service.SentencesService;
import com.bkleszcz.WordApp.service.SynonymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/get")
public class SentencesController {

  private final SentencesService sentencesService;
  private final SynonymService synonymService;
  private final GuessingService guessingService;

  @Autowired
  public SentencesController(SentencesService sentencesService, SynonymController synonymController, SynonymService synonymService, GuessingService guessingService) {
    this.sentencesService = sentencesService;
      this.synonymService = synonymService;
      this.guessingService = guessingService;
  }

  @GetMapping("/sentences")
  public ResponseEntity<String[]> getSentences(@RequestParam String englishWord) {
    String[] sentences = sentencesService.getSentences(englishWord);
    return ResponseEntity.ok(sentences);
  }

  @GetMapping("/correctEnglishWord")
  public ResponseEntity<List<String>> getCorrectEnglishWord(@RequestParam String polishWord) {

    String englishWord = sentencesService.getCorrectEnglishWord(polishWord);

    if(synonymService.checkIfSynonymExistsInDatabase(englishWord)){
        return ResponseEntity.ok(synonymService.getSynonyms(englishWord));
    }
    if (ApiUsageCounterScheduler.getUsageCounter() < ApiUsageCounterScheduler.getMaxUsage()) {
      try {
       return ResponseEntity.ok(synonymService.getSynonyms(englishWord));
      } catch (Exception e) {
        return ResponseEntity.ok(Collections.singletonList(sentencesService.getCorrectEnglishWord(polishWord)));
      }
    }
    return ResponseEntity.ok(Collections.singletonList(sentencesService.getCorrectEnglishWord(polishWord)));

  }

  @GetMapping("/Hint")
  public ResponseEntity<String[]> getHint (@RequestParam String polishWord){
    return ResponseEntity.ok(guessingService.getHints(guessingService.getEntityOfPolishWord(polishWord)));
  }
}
