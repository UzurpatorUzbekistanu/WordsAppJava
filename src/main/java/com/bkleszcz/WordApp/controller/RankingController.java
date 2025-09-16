// src/main/java/com/bkleszcz/WordApp/controller/RankingController.java
package com.bkleszcz.WordApp.controller;                 // pakiet
import com.bkleszcz.WordApp.model.dto.RankRowDto;        // DTO wiersza
import com.bkleszcz.WordApp.service.RankingService;      // serwis rankingu
import org.springframework.web.bind.annotation.*;        // REST
import java.util.*;                                      // List

@RestController                                          // kontroler REST
@RequestMapping("/api/rank")                              // prefiks rankingu
public class RankingController {                          // klasa kontrolera

    private final RankingService ranking;                   // serwis

    public RankingController(RankingService ranking) {      // DI w konstruktorze
        this.ranking = ranking;                               // przypisz
    }

    @GetMapping("/daily")                                   // GET /api/rank/daily
    public List<RankRowDto> daily(@RequestParam(defaultValue = "10") int limit) {
        return ranking.daily(limit);                          // zwróć top N dnia
    }

    @GetMapping("/monthly")                                 // GET /api/rank/monthly
    public List<RankRowDto> monthly(@RequestParam(defaultValue = "10") int limit) {
        return ranking.monthly(limit);                        // zwróć top N miesiąca
    }

    @GetMapping("/top")                                     // GET /api/rank/top
    public List<RankRowDto> top(@RequestParam(defaultValue = "10") int limit) {
        return ranking.top(limit);                            // zwróć top N ogólnie
    }
}
