package com.bkleszcz.WordApp.config;                           // pakiet konfiguracyjny

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;      // wstrzykiwanie z properties
import org.springframework.scheduling.annotation.Scheduled;     // planowanie zadań
import org.springframework.stereotype.Component;                // komponent Springa

import java.time.LocalDate;                                     // data (bez czasu)
import java.util.concurrent.atomic.AtomicInteger;               // bezpieczny licznik wielowątkowy

@Getter
@Component                                                      // rejestruje bean w kontekście
public class ApiUsageCounterScheduler {                         // klasa licznika

    private static final AtomicInteger COUNTER = new AtomicInteger(0); // dzienny licznik zapytań
    private static volatile LocalDate day = LocalDate.now();           // dzień, dla którego liczymy

    private static int MAX;                                    // dzienny limit (wczytamy z properties)

    public ApiUsageCounterScheduler(@Value("${wordsapi.daily-limit:1000}") int maxDaily) {  // pobierz wordsapi.daily-limit (domyślnie 1000)
        MAX = maxDaily;                                           // ustaw limit globalnie
    }



    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Warsaw")    // uruchom codziennie o 00:00 czasu PL
    public void resetDailyCounter() {                           // metoda resetująca
        COUNTER.set(0);                                           // wyzeruj licznik
        day = LocalDate.now();                                    // ustaw bieżący dzień
    }

    public static int incrementUsageCounter() {                 // zwiększ licznik o 1
        return COUNTER.incrementAndGet();                         // zwróć wartość po inkrementacji
    }

    public static int getUsageCounter() {                       // odczytaj aktualny stan
        return COUNTER.get();                                     // zwróć licznik
    }

    public static int getMaxUsage() {                           // odczytaj limit dzienny
        return MAX;                                               // zwróć limit
    }

    public static LocalDate getDay() {                          // odczytaj dzień liczenia
        return day;                                               // zwróć datę
    }
}
