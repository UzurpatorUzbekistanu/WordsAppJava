package com.bkleszcz.WordApp.service;
import com.bkleszcz.WordApp.database.EnglishSynonymsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.model.EnglishSynonyms;
import com.bkleszcz.WordApp.model.EnglishWord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SynonymService {

    private final EnglishSynonymsRepository englishSynonymsRepository;
    private final EnglishWordRepository englishWordRepository;



    public SynonymService(EnglishSynonymsRepository englishSynonymsRepository,
                          EnglishWordRepository englishWordRepository) {
        this.englishSynonymsRepository = englishSynonymsRepository;
        this.englishWordRepository = englishWordRepository;
    }



    public boolean checkIfSynonymExistsInDatabase(long englishWordId) {
        return englishSynonymsRepository.existsByEnglishWordId(englishWordId);
    }

    public boolean checkIfSynonymExistsInDatabase(String englishWord) {
        return englishWordRepository.findFirstByWord(englishWord)
                .map(w -> englishSynonymsRepository.existsByEnglishWordId(Long.valueOf(w.getId())))
                .orElse(false);
    }


    public List<String> getSynonyms(String englishWord) {
        Optional<EnglishWord> wordOpt = englishWordRepository.findFirstByWord(englishWord);
        if (wordOpt.isEmpty()) return Collections.emptyList();
        long englishWordId = wordOpt.get().getId();
        return englishSynonymsRepository.findByEnglishWordId(englishWordId)
                .stream().map(EnglishSynonyms::getSynonym).toList();
    }
}
