package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.service.SentencesService;
import com.bkleszcz.WordApp.service.SynonymService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SynonymController {

    private final SynonymService synonymService;

    public SynonymController(SynonymService synonymService) {
        this.synonymService = synonymService;
    }

    @GetMapping("/synonyms")
    public void getSynonyms(@RequestParam String englishWord) {
        try {
            synonymService.fetchAndSaveSynonyms(englishWord);
            ResponseEntity.ok("Synonyms successfully fetched and saved.");
        } catch (Exception e) {
            ResponseEntity.status(500).body("Error fetching synonyms: " + e.getMessage());
        }
    }
}