package com.bkleszcz.WordApp.domain;

import lombok.Getter;

public enum Strike {

    NONE(0, 1),
    ONE(1,1.12),
    TWO(2, 1.25),
    THREE(3, 1.5),
    FOUR(4, 2.0),
    FIVE(5, 2.5),
    SIX(6, 3.0),
    SEVEN(7, 3.5),
    EIGHT(8, 4.0),
    NINE(9, 4.5),
    TEN(10, 5.0);

    @Getter
    private final int strikeCount;
    @Getter
    private final double multiplier;

    Strike(int strikeCount, double multiplier) {
        this.strikeCount = strikeCount;
        this.multiplier = multiplier;
    }

    public Strike nextStrike(){
        int nextCount = this.strikeCount + 1;
        for (Strike s: Strike.values()){
            if (s.strikeCount == nextCount){
                return s;
            }
        }
        return TEN;
    }

    public Strike resetStrike(){
        return NONE;
    }
}
