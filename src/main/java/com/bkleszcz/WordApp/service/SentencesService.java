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

    Optional<EnglishWord> englishWordEntity = englishWordRepository.findByWord(englishWord);
    if(englishWordEntity.isPresent()){
      table[0] = englishWordEntity.get().getSentenceA1();
      table[1] = englishWordEntity.get().getSentenceHigher();
    }

    return table;
  }

  public String getCorrectEnglishWord(String polishWord) {
    String englishWordString = "";

    Optional<PolishWord> polishWordEntity = polishWordRepository.findByWord(polishWord);
    Integer polishWordId = polishWordEntity.map(PolishWord::getId).orElse(null);
    Optional<Integer> idEnglish = polishEnglishWordRepository.findByPolishWordId(polishWordId);
    if (idEnglish.isPresent()) {
      Optional<EnglishWord> englishWord = englishWordRepository.findById(Long.valueOf(idEnglish.get()));
      englishWordString = englishWord.map(EnglishWord::getWord).orElse(null);
    }
    return englishWordString;
  }
}
