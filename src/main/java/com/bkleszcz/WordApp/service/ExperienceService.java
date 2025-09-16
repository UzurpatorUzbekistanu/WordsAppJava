package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.domain.Level;
import com.bkleszcz.WordApp.domain.User;
import com.bkleszcz.WordApp.model.dto.LevelInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExperienceService {

    @Value("${app.base-exp}")
    private int baseExp;

    private int getReachedExp(User user) {
        return (int) (baseExp * user.getStrikeCurrent().getMultiplier());
    }

    private void increaseStrike(User user) {
        user.setStrikeCurrent(user.getStrikeCurrent().nextStrike());
    }

    private int increaseUserExperience(User user) {
        int experienceGained = getReachedExp(user);
        user.setExperience(user.getExperience() + experienceGained);
        Level updatedLevel = Level.getNextLevelForExperience(user.getExperience(), user.getLevel());
        user.setLevel(updatedLevel);
        return experienceGained;
    }

    private void resetStrike(User user) {
        user.setStrikeCurrent(user.getStrikeCurrent().resetStrike());
    }

    public int doUserExperienceGainedAndStrike(boolean isCorrect, User user){
        int experienceGained = 0;

        if(isCorrect){
            experienceGained = increaseUserExperience(user);
            increaseStrike(user);
        } else {
            resetStrike(user);
        }
        return experienceGained;
    }

    // ⬇️ pomocniczo: policz czy był awans (przed/po liczeniu XP)
    public LevelInfoDto buildLevelInfo(User user, int levelBefore) {
        int levelAfter = user.getLevel().getNumber();              // weź numer po aktualizacji
        boolean leveled = levelAfter > levelBefore;                // porównaj

        // [1/3] TU: pobierz XP z Twojego modelu (przykład: user.getExperienceInLevel())
        int currentInLevel = getCurrentXpInLevel(user);            // ile ma teraz w tym levelu

        // [2/3] TU: odczytaj wymagany XP na KOLEJNY poziom (np. z tabeli Level)
        int requiredForNext = getRequiredXpForNextLevel(user);     // ile trzeba do kolejnego

        return LevelInfoDto.builder()                               // zbuduj DTO
                .levelNumber(levelAfter)                                // numer poziomu
                .currentExperience(currentInLevel)                      // XP bieżący
                .nextLevelExperience(requiredForNext)                   // XP do awansu
                .levelUp(leveled)                                       // czy był awans
                .build();                                               // gotowe
    }

    // [3/3] TE DWIE metody dopasuj do swojego modelu danych:

    // ► pomocnicze: następny level (na max poziomie zwracamy ten sam)
    private Level nextLevel(Level lvl) {                     // wybierz kolejny enum
        Level[] all = Level.values();                          // wszystkie poziomy
        int i = lvl.ordinal();                                 // indeks bieżącego
        return (i < all.length - 1) ? all[i + 1] : lvl;        // na końcu: ten sam
    }

    // ► próg XP dla wejścia w PODANY level (min XP tego levelu)
    private int thresholdFor(Level lvl) {                    // minimalny XP dla levelu
        return lvl.getExperienceRequired();                    // z enuma (Level1 → 0)
    }

    // ► bieżący XP w RAMACH aktualnego levelu (przycina do pojemności)
    private int getCurrentXpInLevel(User user) {
        int total = user.getExperience();                      // sumaryczny XP usera
        int start = thresholdFor(user.getLevel());             // próg startu tego levelu
        Level n = nextLevel(user.getLevel());                  // kolejny level
        int end = (n == user.getLevel())                       // jeśli max level
                ? Integer.MAX_VALUE                                // → brak „końca”
                : thresholdFor(n);                                 // inaczej: próg next

        int capacity = (end == Integer.MAX_VALUE)              // pojemność paska
                ? Integer.MAX_VALUE                                // na max: „nieskończona”
                : Math.max(0, end - start);                        // standardowo: next-start

        int inLevel = Math.max(0, total - start);              // ile nabił od startu
        if (capacity != Integer.MAX_VALUE) {                   // utnij do pojemności
            inLevel = Math.min(inLevel, capacity);
        }

        // DEBUG: podejrzyj liczby w logach, jeśli trzeba
        System.out.printf("[LEVEL] total=%d start=%d end=%s inLevel=%d cap=%s lvl=%d%n",
                total, start, (end==Integer.MAX_VALUE?"INF":String.valueOf(end)),
                inLevel, (capacity==Integer.MAX_VALUE?"INF":String.valueOf(capacity)),
                user.getLevel().getNumber());

        return inLevel;                                        // zwróć „current”
    }

    // ► ile BRKUJE do KOLEJNEGO levelu (remaining; na max → 0)
    private int getRequiredXpForNextLevel(User user) {
        Level n = nextLevel(user.getLevel());                  // kolejny level
        if (n == user.getLevel()) return 0;                    // max → 0
        int target = thresholdFor(n);                          // próg wejścia w next
        int remaining = target - user.getExperience();         // ile brakuje
        return Math.max(0, remaining);                         // nigdy < 0
    }
}