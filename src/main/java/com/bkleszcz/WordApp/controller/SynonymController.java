package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.config.ApiUsageCounterScheduler; // licznik API (limit/dzień)
import com.bkleszcz.WordApp.model.dto.UsageDto;           // DTO do podglądu użycia
import com.bkleszcz.WordApp.service.SynonymService;       // serwis synonimów
import org.springframework.http.ResponseEntity;            // typ odpowiedzi HTTP
import org.springframework.web.bind.annotation.*;          // adnotacje Spring MVC

import java.util.Collections;
import java.util.List;                                     // kolekcje

@RestController
@RequestMapping("/api")
public class SynonymController {

    private final SynonymService synonymService;

    public SynonymController(SynonymService synonymService) {
        this.synonymService = synonymService;
    }

    @GetMapping("/synonyms/list")
    public ResponseEntity<List<List<String>>> list(@RequestParam String englishWord) {
        return ResponseEntity.ok(Collections.singletonList(synonymService.getSynonyms(englishWord)));
    }

}
