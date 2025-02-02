package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.EnglishSynonymsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishEnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishWordRepository;
import com.bkleszcz.WordApp.model.EnglishSynonyms;
import com.bkleszcz.WordApp.model.EnglishWord;
import com.bkleszcz.WordApp.model.PolishWord;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuessingService {

  private final PolishWordRepository polishWordRepository;
  private final PolishEnglishWordRepository polishEnglishWordRepository;
  private final EnglishWordRepository englishWordRepository;
  private final EnglishSynonymsRepository englishSynonymsRepository;
  private final SynonymService synonymService;

  @Autowired
  public GuessingService(PolishWordRepository polishWordRepository,
                         PolishEnglishWordRepository polishEnglishWordRepository,
                         EnglishWordRepository englishWordRepository,
                         EnglishSynonymsRepository englishSynonymsRepository,
                         SynonymService synonymService) {
    this.polishWordRepository = polishWordRepository;
    this.polishEnglishWordRepository = polishEnglishWordRepository;
    this.englishWordRepository = englishWordRepository;
    this.englishSynonymsRepository = englishSynonymsRepository;
    this.synonymService = synonymService;
  }

  public String getRandomPolishWord() {
    List<PolishWord> words = polishWordRepository.findAll();
    int randomIndex = new Random().nextInt(words.size());
    return words.get(randomIndex).getWord();
  }

  public boolean checkTranslation(String polishWord, String englishWordGuess) {

    Optional<PolishWord> polish = polishWordRepository.findByWord(polishWord);

    if (polish.isPresent()) {
      Integer idPolish = polish.get().getId();

      // Znajdź id angielskiego słowa na podstawie polskiego słowa
      Optional<Integer> idEnglish = polishEnglishWordRepository.findByPolishWordId(idPolish);

      if (idEnglish.isPresent()) {
        // Znajdź angielskie słowo po jego id
        Optional<EnglishWord> englishWord = englishWordRepository.findById(Long.valueOf(idEnglish.get()));

        if(synonymService.checkIfSynonymExistsInDatabase(polishWord)){
          boolean answerFlag = false;
          List <EnglishSynonyms> possibleAnswersList = englishSynonymsRepository.findByEnglishWordId(Long.valueOf(idEnglish.get()));

          for(EnglishSynonyms possibleAnswer : possibleAnswersList){
            if(possibleAnswer.getEnglishWord().getWord().equalsIgnoreCase(englishWordGuess)){
              answerFlag = true;
              break;
            }
          }
            return answerFlag;
        }
        // Sprawdź czy słowo pasuje
        return englishWord.map(word -> word.getWord().equalsIgnoreCase(englishWordGuess)).orElse(false);
      } else {
        return false;
      }
    }

    return false;
  }

}


