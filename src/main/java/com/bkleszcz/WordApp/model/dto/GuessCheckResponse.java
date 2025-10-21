package com.bkleszcz.WordApp.model.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GuessCheckResponse {
    private boolean correct;
    private int currentStrike;
    private int experienceGained;
    private List<String> canonicalEnglish;
    private List<String> synonyms;
    private LevelInfoDto level;
    private boolean hintPenaltyApplied;
}
