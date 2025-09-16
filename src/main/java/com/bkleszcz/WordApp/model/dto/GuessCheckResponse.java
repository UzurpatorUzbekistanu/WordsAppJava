// GuessCheckResponse.java  (jeśli już masz – tylko dopisz pola)
package com.bkleszcz.WordApp.model.dto;          // pakiet DTO

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GuessCheckResponse {
    private boolean correct;               // czy trafił EN (informacyjnie dla UI)
    private int currentStrike;             // strike po zapisie
    private int experienceGained;          // XP za próbę
    private List<String> canonicalEnglish; // poprawne EN (gdy pudło)
    private List<String> synonyms;         // synonimy (opcjonalnie)
    private LevelInfoDto level;            // pasek z backendu
    private boolean hintPenaltyApplied;    // ⬅️ czy zastosowano karę
}
