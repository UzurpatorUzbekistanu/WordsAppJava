package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.EnglishSynonymsRepository;
import com.bkleszcz.WordApp.database.EnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishEnglishWordRepository;
import com.bkleszcz.WordApp.database.PolishWordRepository;
import com.bkleszcz.WordApp.model.EnglishSynonyms;
import com.bkleszcz.WordApp.model.EnglishWord;
import com.bkleszcz.WordApp.model.PolishWord;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import com.bkleszcz.WordApp.model.dto.GuessCheckResponse;
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

      Optional<EnglishWord> englishWord = getEnglishWordIdByPolishWord(polishWord);


      if (englishWord.isPresent()) {

        if(synonymService.checkIfSynonymExistsInDatabase(englishWord.get().getId().longValue())){
          boolean answerFlag = false;
          List <EnglishSynonyms> possibleAnswersList = englishSynonymsRepository.findByEnglishWordId(Long.valueOf(englishWord.get().getId()));

          for(EnglishSynonyms possibleAnswer : possibleAnswersList){
            if(possibleAnswer.getEnglishWord().getWord().equalsIgnoreCase(englishWordGuess)){
              answerFlag = true;
              break;
            }
          }
            return answerFlag;
        }
        return englishWord.map(word -> word.getWord().equalsIgnoreCase(englishWordGuess)).orElse(false);
      } else {
        return false;
      }
  }

  public GuessCheckResponse buildAndCheckResponseForUnauthenticatedUser(String polishWord, String englishWord){

     GuessCheckResponse.GuessCheckResponseBuilder b = GuessCheckResponse.builder()
            .correct(checkTranslation(polishWord, englishWord))
            .currentStrike(0)
            .experienceGained(0)
            .canonicalEnglish(null)
            .synonyms(null)
            .level(null)
            .hintPenaltyApplied(false);

    return b.build();
  }
  public String[] getHints(PolishWord polishWord) {
    return Objects.requireNonNull(getEnglishWordByPolishWord(polishWord)).split(" ");
  }

  private Optional<EnglishWord> getEnglishWordIdByPolishWord(String polishWord) {
    int idPolish = getIdOfPolishWord(polishWord);
    Optional<Integer> idEnglish = polishEnglishWordRepository.findByPolishWordId(idPolish);

    if (idEnglish.isPresent()) {
      Optional<EnglishWord> englishWord = englishWordRepository.findById(Long.valueOf(idEnglish.get()));
      return englishWord;
    }
    return Optional.empty();
  }


  private int getIdOfPolishWord(String polishWord) {
    Optional<PolishWord> polish = polishWordRepository.findFirstByWord(polishWord);
    if(polish.isPresent()){
      return polish.get().getId();
    }else {
      return 0;
    }
  }

  public PolishWord getEntityOfPolishWord (String polishWord){
    Optional<PolishWord> polishWordOptional = polishWordRepository.findFirstByWord(polishWord);

    return polishWordOptional.orElse(null);
  }

  private String getEnglishWordByPolishWord(PolishWord polishWord) {
    Integer idPolish = polishWord.getId();
    Optional<Integer> idEnglish = polishEnglishWordRepository.findByPolishWordId(idPolish);
    if (idEnglish.isPresent()) {
      Optional<EnglishWord> englishWord = englishWordRepository.findById(Long.valueOf(idEnglish.get()));
      return englishWord.map(EnglishWord::getWord).orElse(null);
    } else {
      return null;
    }
  }

}


