package com.bkleszcz.WordApp.database.projection;

public interface UserScoreView {                          // interfejs projekcji JPA
    Long getUserId();                                      // id usera
    String getUsername();                                  // nazwa
    Long getScore();                                       // suma XP
}
