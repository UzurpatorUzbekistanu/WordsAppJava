package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.config.ApiUsageCounterScheduler;
import com.bkleszcz.WordApp.database.EnglishSynonymsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.model.EnglishSynonyms;
import com.bkleszcz.WordApp.model.EnglishWord;
import com.bkleszcz.WordApp.model.WordResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SynonymService {

    private final EnglishSynonymsRepository englishSynonymsRepository;
    private final EnglishWordRepository englishWordRepository;

    @Autowired
    public SynonymService(EnglishSynonymsRepository englishSynonymsRepository, EnglishWordRepository englishWordRepository) {
        this.englishSynonymsRepository = englishSynonymsRepository;
        this.englishWordRepository = englishWordRepository;
    }


    public void setSynonymsIntoDatabase(WordResponse wordResponse, String englishWord) {

        Optional<EnglishWord> englishWordEntityOptional = englishWordRepository.findByWord(englishWord);
        if (englishWordEntityOptional.isPresent()) {
            EnglishWord englishWordEntity = englishWordEntityOptional.get();

            for (String synonym : wordResponse.getSynonyms()) {
                EnglishSynonyms englishSynonym = new EnglishSynonyms();
                englishSynonym.setEnglishWord(englishWordEntity);
                englishSynonym.setSynonym(synonym);
                englishSynonymsRepository.save(englishSynonym);
            }
        } else {
            throw new IllegalArgumentException("English word not found: " + englishWord);
        }
    }
// PONIZEJ PRAWDOPODOBNIE BLEDNIE ODWOLUJESZ SIE DO POLISH WORD
    public boolean checkIfSynonymExistsInDatabase(String polishWord) {
        Optional<EnglishWord> englishWord = englishWordRepository.findByWord(polishWord);
        Long englishWordId = 0L;
        if(englishWord.isPresent()){
            englishWordId = (long) englishWord.get().getId().intValue();
        }
        return englishSynonymsRepository.existsByEnglishWordId(englishWordId);
    }

    public String getSynonyms(String polishWord) {
        Optional<EnglishWord> englishWord = englishWordRepository.findByWord(polishWord);
        Long englishWordId = 0L;
        if(englishWord.isPresent()){
            englishWordId = (long) englishWord.get().getId().intValue();
        }
        List<EnglishSynonyms> synonymsList = englishSynonymsRepository.findByEnglishWordId(englishWordId);

        StringBuilder synonyms = new StringBuilder();
        for (EnglishSynonyms synonym : synonymsList) {
            synonyms.append(synonym.getSynonym()).append(", ");
        }
        return synonyms.toString();
    }

    public void fetchAndSaveSynonyms(String englishWord) throws IOException, InterruptedException, URISyntaxException {
        ApiUsageCounterScheduler.incrementUsageCounter();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://wordsapiv1.p.rapidapi.com/words/" + englishWord + "/synonyms"))
                .header("x-rapidapi-host", "wordsapiv1.p.rapidapi.com")
                .header("x-rapidapi-key", "a72264e6abmshaed5de209a3cb89p16a60fjsn01395b5b2d10")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        WordResponse wordResponse = objectMapper.readValue(response.body(), WordResponse.class);

        setSynonymsIntoDatabase(wordResponse, englishWord);
    }

}
