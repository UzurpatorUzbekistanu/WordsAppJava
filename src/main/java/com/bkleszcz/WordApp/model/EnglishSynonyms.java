package com.bkleszcz.WordApp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity                                      // Oznacza klasę jako encję JPA.
@Table(name = "english_synonyms")             // Mapuje encję na tabelę "english_synonyms".
@Data                                        // Lombok generuje gettery, settery, itp.
public class EnglishSynonyms {

    @Id                                    // Oznacza pole jako klucz główny.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoinkrementacja klucza.
    private Long id;                         // Unikalne ID encji.

    @Getter
    @Column(nullable = false)               // Definiuje kolumnę "synonym" jako nie-null.
    private String synonym;                  // Przechowuje tekst synonimu.

    @ManyToOne                             // Relacja wiele do jednego z encją EnglishWord.
    @JoinColumn(name = "english_word_id", nullable = false) // Kolumna klucza obcego.
    private EnglishWord englishWord;         // Powiązana encja EnglishWord.
}
