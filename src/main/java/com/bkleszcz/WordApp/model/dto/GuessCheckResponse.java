package com.bkleszcz.WordApp.model.dto;

import lombok.Data;

@Data
public class GuessCheckResponse {
    private boolean correct;
    private int experienceGained;
    private int currentStrike;

    public GuessCheckResponse(boolean correct, int experienceGained, int currentStrike) {
        this.correct = correct;
        this.experienceGained = experienceGained;
        this.currentStrike = currentStrike;
    }
}
