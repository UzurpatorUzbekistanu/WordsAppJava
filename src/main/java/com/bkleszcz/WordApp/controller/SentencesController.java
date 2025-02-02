package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.config.ApiUsageCounterScheduler;
import com.bkleszcz.WordApp.service.SentencesService;
import com.bkleszcz.WordApp.service.SynonymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/get")
public class SentencesController {

  private final SentencesService sentencesService;
  private final SynonymController synonymController;
  private final SynonymService synonymService;

  @Autowired
  public SentencesController(SentencesService sentencesService, SynonymController synonymController, SynonymService synonymService) {
    this.sentencesService = sentencesService;
      this.synonymController = synonymController;
      this.synonymService = synonymService;
  }

  @GetMapping("/sentences")
  public ResponseEntity<String[]> getSentences(@RequestParam String englishWord) {
    String[] sentences = sentencesService.getSentences(englishWord);
    return ResponseEntity.ok(sentences);
  }

  @GetMapping("/correctEnglishWord")
  public ResponseEntity<String> getCorrectEnglishWord(@RequestParam String polishWord) {

    String englishWord = sentencesService.getCorrectEnglishWord(polishWord);

    if(synonymService.checkIfSynonymExistsInDatabase(englishWord)){
        return ResponseEntity.ok(synonymService.getSynonyms(englishWord));
    }
    if (ApiUsageCounterScheduler.getUsageCounter() < ApiUsageCounterScheduler.getMAX_USAGE()) {
      try {
       synonymController.getSynonyms(englishWord);
       return ResponseEntity.ok(synonymService.getSynonyms(englishWord));
      } catch (Exception e) {
        return ResponseEntity.ok(sentencesService.getCorrectEnglishWord(polishWord));
      }
    }
    return ResponseEntity.ok(sentencesService.getCorrectEnglishWord(polishWord));

  }

}
