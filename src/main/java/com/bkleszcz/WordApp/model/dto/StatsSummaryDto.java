package com.bkleszcz.WordApp.model.dto;                 // pakiet DTO

import lombok.AllArgsConstructor;                       // gen. konstruktora
import lombok.Data;                                     // gen. get/set

@Data                                                  // DTO pod tabelę
@AllArgsConstructor
public class StatsSummaryDto {
    private long attemptsOfNewWords;                     // próby nowych słów (nr=1)
    private long correctGuesses;                         // poprawne odpowiedzi
    private long reviewAttempts;                         // próby powtórkowe (nr>1)
    private long completedReviews;                       // poprawne w powtórkach
}
