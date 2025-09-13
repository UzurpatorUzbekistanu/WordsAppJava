package com.bkleszcz.WordApp.service;

import com.bkleszcz.WordApp.domain.Level;
import com.bkleszcz.WordApp.domain.User;
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
}