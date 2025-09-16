package com.bkleszcz.WordApp.controller;                 // pakiet kontrolera

import com.bkleszcz.WordApp.service.DictionaryService;   // serwis słownika
import org.springframework.http.ResponseEntity;          // opakowanie odpowiedzi
import org.springframework.web.bind.annotation.*;        // adnotacje REST
import java.util.List;                                   // lista Stringów

@RestController                                          // kontroler REST
@RequestMapping("/api/dictionary")                       // wspólny prefix
public class DictionaryController {                      // klasa kontrolera

    private final DictionaryService dictionaryService;   // pole serwisu

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;      // wstrzyknięcie przez ctor
    }

    @GetMapping("/english")                              // GET /api/dictionary/english
    public ResponseEntity<List<String>> englishForPolish(
            @RequestParam("polish") String polish        // parametr ?polish=...
    ) {
        if (polish == null || polish.isBlank()) {        // walidacja wejścia
            return ResponseEntity.badRequest().build();  // 400 gdy pusty
        }
        List<String> result = dictionaryService          // delegacja do serwisu
                .findEnglishByPolish(polish.trim());
        return ResponseEntity.ok(result);                // 200 + lista EN (może być pusta)
    }
}
