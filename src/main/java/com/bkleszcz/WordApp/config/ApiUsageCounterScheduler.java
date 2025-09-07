package com.bkleszcz.WordApp.config;


import lombok.Getter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ApiUsageCounterScheduler {

    @Getter
    private static int usageCounter = 0;
    @Getter
    private static final int MAX_USAGE = 2000;

    @Scheduled(cron = "0 0 0 * * *")
    public void resetCounter() {
        usageCounter = 0;

    }

    public static void incrementUsageCounter() {
        usageCounter++;
    }

}
