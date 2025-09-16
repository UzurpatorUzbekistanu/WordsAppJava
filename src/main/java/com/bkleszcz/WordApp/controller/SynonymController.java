package com.bkleszcz.WordApp.controller;                 // pakiet kontrolerów

import com.bkleszcz.WordApp.config.ApiUsageCounterScheduler; // licznik API (limit/dzień)
import com.bkleszcz.WordApp.model.dto.UsageDto;           // DTO do podglądu użycia
import com.bkleszcz.WordApp.service.SynonymService;       // serwis synonimów
import org.springframework.http.ResponseEntity;            // typ odpowiedzi HTTP
import org.springframework.web.bind.annotation.*;          // adnotacje Spring MVC

import java.util.Collections;
import java.util.List;                                     // kolekcje

@RestController                                            // znacznik kontrolera REST
@RequestMapping("/api")                                    // wspólny prefix /api
public class SynonymController {                           // POCZĄTEK klasy kontrolera

    private final SynonymService synonymService;           // zależność: serwis

    public SynonymController(SynonymService synonymService) { // konstruktor DI
        this.synonymService = synonymService;              // przypisanie pola
    }

    @GetMapping("/synonyms")                               // GET /api/synonyms
    public ResponseEntity<String> fetch(@RequestParam String englishWord) { // pobierz+zapisz z zewn. API
        try {                                              // obsługa wyjątków
            synonymService.fetchAndSaveSynonyms(englishWord); // akcja serwisu
            return ResponseEntity.ok("Synonyms fetched & saved."); // 200 OK z komunikatem
        } catch (IllegalStateException e) {                // limit dobowy przekroczony
            return ResponseEntity.status(429).body(e.getMessage()); // 429 Too Many Requests
        } catch (Exception e) {                            // inne błędy
            return ResponseEntity.status(500).body("Error: " + e.getMessage()); // 500
        }
    }

    @GetMapping("/synonyms/list")                          // GET /api/synonyms/list
    public ResponseEntity<List<List<String>>> list(@RequestParam String englishWord) { // lista z DB
        return ResponseEntity.ok(Collections.singletonList(synonymService.getSynonyms(englishWord))); // 200 + JSON listy
    }

    @GetMapping("/synonyms/usage")                         // GET /api/synonyms/usage
    public ResponseEntity<UsageDto> usage() {              // zwróć stan licznika
        int used = ApiUsageCounterScheduler.getUsageCounter(); // ile wykorzystano dziś
        int limit = ApiUsageCounterScheduler.getMaxUsage();    // dzienny limit
        int remaining = Math.max(0, limit - used);             // ile zostało
        String day = ApiUsageCounterScheduler.getDay().toString(); // bieżący dzień
        return ResponseEntity.ok(new UsageDto(day, used, limit, remaining)); // 200 + DTO
    }
}                                                          // KONIEC klasy kontrolera
