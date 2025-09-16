// src/main/java/com/bkleszcz/WordApp/service/RankingService.java
package com.bkleszcz.WordApp.service;                    // pakiet
import com.bkleszcz.WordApp.database.AttemptsRepository; // repo prób
import com.bkleszcz.WordApp.database.projection.UserScoreView; // projekcja
import com.bkleszcz.WordApp.model.dto.RankRowDto;        // DTO wiersza
import org.springframework.stereotype.Service;           // @Service
import java.time.*;                                      // LocalDate/Zone
import java.util.*;                                      // List/Date
import java.util.stream.*;                               // stream

@Service
public class RankingService {
    private final AttemptsRepository repo;
    public RankingService(AttemptsRepository repo) { this.repo = repo; }

    public List<RankRowDto> daily(int limit) {
        LocalDate d = LocalDate.now();
        return map(repo.sumXpByUserBetween(toDate(d.atStartOfDay()),
                        toDate(d.plusDays(1).atStartOfDay())),
                limit);
    }

    public List<RankRowDto> monthly(int limit) {
        LocalDate first = LocalDate.now().withDayOfMonth(1);
        LocalDate next  = first.plusMonths(1);
        return map(repo.sumXpByUserBetween(toDate(first.atStartOfDay()),
                        toDate(next.atStartOfDay())),
                limit);
    }

    public List<RankRowDto> top(int limit) {
        return map(repo.sumAllXpByUser(), limit);
    }

    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private List<RankRowDto> map(List<Object[]> rows, int limit) {
        int n = Math.min(limit, rows.size());
        List<RankRowDto> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Object[] r = rows.get(i);                        // [0]=id, [1]=userName, [2]=score
            out.add(RankRowDto.builder()
                    .position(i + 1)
                    .userId(((Number) r[0]).longValue())
                    .username((String) r[1])
                    .score(r[2] == null ? 0L : ((Number) r[2]).longValue())
                    .build());
        }
        return out;
    }
}

