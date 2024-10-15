package com.bkleszcz.WordApp.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "polish_english_word")
public class PolishEnglishWord {

  @EmbeddedId
  private PolishEnglishWordId id = new PolishEnglishWordId();

  @ManyToOne
  @MapsId("polishWordId")
  @JoinColumn(name = "polish_word_id")
  private PolishWord polishWord;

  @ManyToOne
  @MapsId("englishWordId")
  @JoinColumn(name = "english_word_id")
  private EnglishWord englishWord;

}

