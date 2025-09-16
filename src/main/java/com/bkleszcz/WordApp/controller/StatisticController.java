package com.bkleszcz.WordApp.controller;

import com.bkleszcz.WordApp.model.dto.AttemptsDto;
import com.bkleszcz.WordApp.model.dto.DailyStatsDto;
import com.bkleszcz.WordApp.model.dto.StatsSummaryDto;
import com.bkleszcz.WordApp.service.AttemptsService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/user/{userId}/daily")
    public List<DailyStatsDto> getDaily(@PathVariable Long userId) {
        return attemptsService.getDailyStats(userId);
    }

    @GetMapping("/user/{userId}/summary")
    public StatsSummaryDto getSummary(@PathVariable Long userId,
                                      @RequestParam(required = false, defaultValue = "daily") String range) {
        return attemptsService.getSummaryForUser(userId, range);
    }


}
