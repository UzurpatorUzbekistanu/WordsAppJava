package com.bkleszcz.WordApp.domain;

import lombok.Getter;

public enum Level {
    LEVEL_1(1,0),
    LEVEL_2(2, 100),
    LEVEL_3(3, 230),
    LEVEL_4(4, 400),
    LEVEL_5(5, 700),
    LEVEL_6(6, 1100),
    LEVEL_7(7, 1600),
    LEVEL_8(8, 2200),
    LEVEL_9(9, 3000),
    LEVEL_10(10, 4000),
    LEVEL_11(11, 5200),
    LEVEL_12(12, 6600),
    LEVEL_13(13, 8200),
    LEVEL_14(14, 10000),
    LEVEL_15(15, 12000),
    LEVEL_16(16, 15000),
    LEVEL_17(17, 18000),
    LEVEL_18(18, 22000),
    LEVEL_19(19, 27000),
    LEVEL_20(20, 33000),
    LEVEL_21(21, 40000),
    LEVEL_22(22, 48000),
    LEVEL_23(23, 57000),
    LEVEL_24(24, 68000),
    LEVEL_25(25, 80000),
    LEVEL_26(26, 95000),
    LEVEL_27(27, 110000),
    LEVEL_28(28, 130000),
    LEVEL_29(29, 150000),
    LEVEL_30(30, 180000),
    LEVEL_31(31, 210000),
    LEVEL_32(32, 250000),
    LEVEL_33(33, 300000),
    LEVEL_34(34, 360000),
    LEVEL_35(35, 430000),
    LEVEL_36(36, 510000),
    LEVEL_37(37, 600000),
    LEVEL_38(38, 700000),
    LEVEL_39(39, 820000),
    LEVEL_40(40, 950000),
    LEVEL_41(41, 1100000),
    LEVEL_42(42, 1300000),
    LEVEL_43(43, 1500000),
    LEVEL_44(44, 1800000),
    LEVEL_45(45, 2100000),
    LEVEL_46(46, 2500000),
    LEVEL_47(47, 3000000),
    LEVEL_48(48, 3600000),
    LEVEL_49(49, 4300000),
    LEVEL_50(50, 5100000);

    @Getter
    private final int number;
    @Getter
    private final int experienceRequired;

    Level(int number, int experienceRequired) {
        this.number = number;
        this.experienceRequired = experienceRequired;
    }

    public static Level getNextLevelForExperience(int totalExperience, Level currentLevel) {
        Level[] levels = Level.values();
        int currentIndex = currentLevel.ordinal();

        for (int i = currentIndex + 1; i < levels.length; i++) {
            if (totalExperience < levels[i].getExperienceRequired()) {
                return levels[i - 1];
            }
        }
        return levels[levels.length - 1];
    }




}
