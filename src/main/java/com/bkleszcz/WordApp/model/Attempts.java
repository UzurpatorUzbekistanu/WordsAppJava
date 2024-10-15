package com.bkleszcz.WordApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.Data;

@Entity
@Data
public class Attempts {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "polish_word_id", nullable = false)
  private PolishWord polishWord;

  @ManyToOne
  @JoinColumn(name = "english_word_id", nullable = false)
  private EnglishWord englishWord;

  private Date dateLastTry;
  private Date dateLastSuccess;
  private int numberOfAttempts;
  private int correctAnswers;
  private int wrongAnswers;
  private float priority;

}

