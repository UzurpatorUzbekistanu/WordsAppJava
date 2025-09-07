package com.bkleszcz.WordApp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "english_synonyms")
@Data
public class EnglishSynonyms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false)
    private String synonym;

    @ManyToOne
    @JoinColumn(name = "english_word_id", nullable = false)
    private EnglishWord englishWord;
}
