package com.bkleszcz.WordApp.service;                                      // pakiet serwisu

import com.bkleszcz.WordApp.config.ApiUsageCounterScheduler;               // licznik limitu API
import com.bkleszcz.WordApp.database.EnglishSynonymsRepository;            // repo synonimów
import com.bkleszcz.WordApp.database.EnglishWordRepository;                // repo słów
import com.bkleszcz.WordApp.model.EnglishSynonyms;                         // encja synonimu
import com.bkleszcz.WordApp.model.EnglishWord;                             // encja słowa
import com.bkleszcz.WordApp.model.WordResponse;                            // DTO zewnętrznego API
import com.fasterxml.jackson.databind.ObjectMapper;                         // parser JSON
import org.springframework.beans.factory.annotation.Value;                  // wstrzykiwanie z properties
import org.springframework.stereotype.Service;                              // komponent serwisowy
import org.springframework.transaction.annotation.Transactional;            // transakcje

import java.io.IOException;                                                // wyjątki IO
import java.net.URI;                                                       // URI do HTTP
import java.net.URISyntaxException;                                        // wyjątek URI
import java.net.http.HttpClient;                                           // klient HTTP
import java.net.http.HttpRequest;                                          // żądanie HTTP
import java.net.http.HttpResponse;                                         // odpowiedź HTTP
import java.util.Collections;                                              // kolekcje narzędziowe
import java.util.List;                                                     // listy
import java.util.Locale;                                                   // normalizacja case
import java.util.Optional;                                                 // Optional
import java.util.stream.Collectors;                                        // stream collect

@Service                                                                    // rejestruj serwis w Springu
@Transactional                                                             // domyślnie transakcyjny
public class SynonymService {                                               // klasa serwisu

    private final EnglishSynonymsRepository englishSynonymsRepository;      // pole repo synonimów
    private final EnglishWordRepository englishWordRepository;              // pole repo słów
    private final ObjectMapper objectMapper = new ObjectMapper();           // parser JSON (wspólny)

    @Value("${wordsapi.key}")                 // ← poprawna nazwa property
    private String wordsApiKey;                                             // klucz do WordsAPI

    public SynonymService(EnglishSynonymsRepository englishSynonymsRepository, // ✅ konstruktor bez String
                          EnglishWordRepository englishWordRepository) {    // wstrzyknij tylko repozytoria
        this.englishSynonymsRepository = englishSynonymsRepository;         // przypisz pole
        this.englishWordRepository = englishWordRepository;                 // przypisz pole
    }

    public void setSynonymsIntoDatabase(WordResponse wordResponse,          // zapis synonimów do DB
                                        String englishWord) {
        Optional<EnglishWord> englishWordEntityOptional =                   // znajdź encję słowa
                englishWordRepository.findFirstByWord(englishWord);
        if (englishWordEntityOptional.isEmpty()) {                          // brak słowa w DB?
            throw new IllegalArgumentException("English word not found: " + englishWord); // błąd
        }
        EnglishWord englishWordEntity = englishWordEntityOptional.get();    // mamy encję słowa

        List<String> synonyms = wordResponse.getSynonyms();                 // pobierz listę synonimów
        if (synonyms == null || synonyms.isEmpty()) return;                 // nic do zapisania → wyjdź

        List<EnglishSynonyms> toInsert = synonyms.stream()                  // stream po synonimach
                .map(s -> s.toLowerCase(Locale.ROOT))                       // normalizuj do lower-case
                .distinct()                                                 // usuń duplikaty z listy
                .filter(s -> !englishSynonymsRepository                     // pomiń te już w DB
                        .existsByEnglishWordIdAndSynonymIgnoreCase(Long.valueOf(englishWordEntity.getId()), s))
                .map(s -> {                                                 // zamień na encje
                    EnglishSynonyms es = new EnglishSynonyms();             // nowa encja
                    es.setEnglishWord(englishWordEntity);                   // powiąż z słowem
                    es.setSynonym(s);                                       // ustaw tekst synonimu
                    return es;                                              // zwróć encję
                })
                .collect(Collectors.toList());                              // zbierz do listy

        if (!toInsert.isEmpty()) {                                          // jeśli coś nowego
            englishSynonymsRepository.saveAll(toInsert);                    // zapisz batchowo
        }
    }

    public boolean checkIfSynonymExistsInDatabase(long englishWordId) {     // czy słowo ma synonimy?
        return englishSynonymsRepository.existsByEnglishWordId(englishWordId); // delegacja do repo
    }

    public boolean checkIfSynonymExistsInDatabase(String englishWord) {
        return englishWordRepository.findFirstByWord(englishWord)
                .map(w -> englishSynonymsRepository.existsByEnglishWordId(Long.valueOf(w.getId())))
                .orElse(false);
    }


    public List<String> getSynonyms(String englishWord) {                    // zwróć listę synonimów
        Optional<EnglishWord> wordOpt = englishWordRepository.findFirstByWord(englishWord); // znajdź słowo
        if (wordOpt.isEmpty()) return Collections.emptyList();               // brak słowa → pusta lista
        long englishWordId = wordOpt.get().getId();                          // ID słowa
        return englishSynonymsRepository.findByEnglishWordId(englishWordId)  // pobierz encje synonimów
                .stream().map(EnglishSynonyms::getSynonym).toList();         // zmapuj na String
    }

    public void fetchAndSaveSynonyms(String englishWord)                     // fetch z WordsAPI + zapis
            throws IOException, InterruptedException, URISyntaxException {
        if (ApiUsageCounterScheduler.getUsageCounter()                       // sprawdź limit przed wywołaniem
                >= ApiUsageCounterScheduler.getMaxUsage()) {
            throw new IllegalStateException("Daily synonyms API limit reached"); // sygnalizuj 429
        }

        HttpClient client = HttpClient.newHttpClient();                      // klient HTTP
        HttpRequest request = HttpRequest.newBuilder()                       // budowa żądania
                .uri(new URI("https://wordsapiv1.p.rapidapi.com/words/"     // adres endpointu
                        + englishWord + "/synonyms"))
                .header("x-rapidapi-host", "wordsapiv1.p.rapidapi.com")     // nagłówek hosta
                .header("x-rapidapi-key", wordsApiKey)                       // ✅ klucz z properties
                .GET()                                                       // metoda GET
                .build();                                                    // zbuduj żądanie

        HttpResponse<String> response = client.send(                         // wyślij żądanie
                request, HttpResponse.BodyHandlers.ofString());              // odbierz jako String

        ApiUsageCounterScheduler.incrementUsageCounter();                    // ✅ policz realne wywołanie

        WordResponse wordResponse =                                         // zdeserializuj JSON
                objectMapper.readValue(response.body(), WordResponse.class);

        setSynonymsIntoDatabase(wordResponse, englishWord);                  // zapisz do DB
    }
}
