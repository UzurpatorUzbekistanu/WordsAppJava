package com.bkleszcz.WordApp.service;                              // pakiet serwisów

import com.bkleszcz.WordApp.database.PolishWordRepository;         // repo słów PL
import com.bkleszcz.WordApp.database.PolishEnglishWordRepository;  // repo relacji PL–EN
import com.bkleszcz.WordApp.model.EnglishWord;                     // encja EN
import com.bkleszcz.WordApp.model.PolishWord;                      // encja PL
import com.bkleszcz.WordApp.model.PolishEnglishWord;               // encja relacji
import org.springframework.stereotype.Service;                      // komponent serwisowy

import java.util.*;                                                // kolekcje + Optional
import java.util.stream.Collectors;                                // stream → list

@Service                                                            // rejestracja serwisu
public class DictionaryService {                                    // klasa serwisu

    private final PolishWordRepository polishRepo;                    // pole repo PL
    private final PolishEnglishWordRepository relRepo;                // pole repo relacji

    public DictionaryService(PolishWordRepository polishRepo,         // konstruktor DI
                             PolishEnglishWordRepository relRepo) {
        this.polishRepo = polishRepo;                                   // przypisz repo PL
        this.relRepo = relRepo;                                         // przypisz repo relacji
    }

    public List<String> findEnglishByPolish(String polishWord) {      // główna metoda
        if (polishWord == null || polishWord.isBlank())                 // walidacja wejścia
            return Collections.emptyList();                               // pusty wynik

        return polishRepo.findFirstByWord(polishWord.trim())            // Optional<PolishWord>
                .map(pl -> {                                                  // jeśli znaleziono PL...
                    List<PolishEnglishWord> rels =                              // spróbuj po ID PL
                            relRepo.findAllByPolishWordId(Long.valueOf(pl.getId()));                 // lista relacji
                    if (rels == null || rels.isEmpty()) {                       // gdy pusto/nul
                        rels = relRepo.findByPolishWord(pl);                      // fallback: po encji
                    }
                    if (rels == null || rels.isEmpty())                         // nadal brak relacji?
                        return Collections.<String>emptyList();                   // zwróć pustą listę

                    return rels.stream()                                        // przemapuj relacje…
                            .map(PolishEnglishWord::getEnglishWord)                 // …na encje EN
                            .filter(Objects::nonNull)                               // odfiltruj null
                            .map(EnglishWord::getWord)                              // pobierz String słowa
                            .filter(Objects::nonNull)                               // odfiltruj null
                            .map(String::trim)                                      // przytnij spacje
                            .filter(s -> !s.isEmpty())                              // usuń puste
                            .distinct()                                             // deduplikuj
                            .collect(Collectors.toList());                          // do listy
                })
                .orElse(Collections.emptyList());                             // Optional pusty → []
    }
}
