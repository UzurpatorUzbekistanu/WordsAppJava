package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.model.dto.AttemptsDto;
import com.bkleszcz.WordApp.service.AttemptsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {

    private final AttemptsService attemptsService;

    public StatisticController(AttemptsService attemptsService) {
        this.attemptsService = attemptsService;
    }

    @GetMapping("/user/{userId}/attempts")
    public List<AttemptsDto> getAttemptsStats(@PathVariable Long userId){
        return attemptsService.getAttemptsDtosByUserId(userId);
    }

    @GetMapping("/YearlyExperienceRank")
    public List<AttemptsDto> getYearlyAttemptsStats(){
        return attemptsService.getYearlyAttemptsDtos();
    }
}
