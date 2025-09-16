package com.bkleszcz.WordApp.model.dto;                 // pakiet DTO
import lombok.*;                                        // Lombok

@Data @AllArgsConstructor @NoArgsConstructor @Builder   // get/set/builder
public class RankRowDto {                                // jeden wiersz tabeli
    private int position;                                  // pozycja w rankingu (1..n)
    private Long userId;                                   // id użytkownika
    private String username;                               // nazwa użytkownika
    private long score;                                    // wynik (suma XP)
}