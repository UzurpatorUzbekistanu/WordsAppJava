package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.service.SentencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/get")
public class SentencesController {

  private final SentencesService sentencesService;

  @Autowired
  public SentencesController(SentencesService sentencesService) {
    this.sentencesService = sentencesService;
  }

  @GetMapping("/sentences")
  public ResponseEntity<String[]> getSentences(@RequestParam String englishWord) {
    String[] sentences = sentencesService.getSentences(englishWord);
    return ResponseEntity.ok(sentences);
  }

  @GetMapping("/correctEnglishWord")
  public ResponseEntity<String> getCorrectEnglishWord(@RequestParam String polishWord) {
    return ResponseEntity.ok(sentencesService.getCorrectEnglishWord(polishWord));
  }

}
