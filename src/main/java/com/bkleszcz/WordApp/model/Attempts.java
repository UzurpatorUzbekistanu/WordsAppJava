package com.bkleszcz.WordApp.model;

import com.bkleszcz.WordApp.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User appUser;

  private int level;

  private Date dateLastTry;
  private Date dateLastSuccess;
  private Date dateRepeat;
  private int numberOfAttempts;
  private int correctAnswers;
  private int wrongAnswers;
  private int experienceGained;
  private int withStrike;

}

