package com.bkleszcz.WordApp.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AttemptsDto {
    private Long userId;
    private int level;
    private int experienceGained;
    private int withStrike;
    private Date dateLastTry;


}

