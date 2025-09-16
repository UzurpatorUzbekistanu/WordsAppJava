package com.bkleszcz.WordApp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyStatsDto {
    private String date;
    private long correctAnswers;
    private long incorrectAnswers;
}
