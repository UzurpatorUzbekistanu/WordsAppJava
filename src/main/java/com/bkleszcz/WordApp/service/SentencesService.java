package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.database.EnglishWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SentencesService {

  private final EnglishWordRepository englishWordRepository;

  @Autowired
  public SentencesService(EnglishWordRepository englishWordRepository){
    this.englishWordRepository = englishWordRepository;
  }

  public String[] getSentences(String englishWord){
    String[] table = new String[2];

    table[0] = englishWordRepository.findByWord(englishWord).get().getSentenceA1();
    table[1] = englishWordRepository.findByWord(englishWord).get().getSentenceHigher();

    return table;
  }
}
