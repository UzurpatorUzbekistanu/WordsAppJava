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

    private void increaseUserExperience(User user) {
        user.setExperience(user.getExperience() + getReachedExp(user));
        Level updatedLevel = Level.getNextLevelForExperience(user.getExperience(), user.getLevel());
        user.setLevel(updatedLevel);
    }

    private void resetStrike(User user) {
        user.setStrikeCurrent(user.getStrikeCurrent().resetStrike());
    }

    public void doUserExperienceGainedAndStrike(boolean isCorrect, User user){
        if(isCorrect){
            increaseUserExperience(user);
            increaseStrike(user);
        } else {
            resetStrike(user);
        }
    }

}