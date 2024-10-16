package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishEnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishWordRepository;
import com.bkleszcz.WordApp.model.EnglishWord;
import com.bkleszcz.WordApp.model.PolishWord;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SentencesService {

  private final EnglishWordRepository englishWordRepository;
  private final PolishEnglishWordRepository polishEnglishWordRepository;
  private final PolishWordRepository polishWordRepository;

  @Autowired
  public SentencesService(PolishWordRepository polishWordRepository,
                          PolishEnglishWordRepository polishEnglishWordRepository,
                          EnglishWordRepository englishWordRepository) {
    this.polishWordRepository = polishWordRepository;
    this.polishEnglishWordRepository = polishEnglishWordRepository;
    this.englishWordRepository = englishWordRepository;
  }


  public String[] getSentences(String englishWord) {
    String[] table = new String[2];

    table[0] = englishWordRepository.findByWord(englishWord).get().getSentenceA1();
    table[1] = englishWordRepository.findByWord(englishWord).get().getSentenceHigher();

    return table;
  }

  public String getCorrectEnglishWord(String polishWord) {
    String englishWordString = "";

    Optional<PolishWord> polishWordEntity = polishWordRepository.findByWord(polishWord);
    int polishWordId = polishWordEntity.get().getId();
    Optional<Integer> idEnglish = polishEnglishWordRepository.findByPolishWordId(polishWordId);
    if (idEnglish.isPresent()) {
      // Znajdź angielskie słowo po jego id
      Optional<EnglishWord> englishWord = englishWordRepository.findById(Long.valueOf(idEnglish.get()));
      englishWordString = englishWord.get().getWord();
    }
    return englishWordString;
  }
}
