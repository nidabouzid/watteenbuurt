package com.nbodev.watteenbuurt.domain.weather;

public enum Season {
    SPRING, SUMMER, AUTUMN, WINTER;

    /**
     * Derive season from month (1–12).
     */
    public static Season fromMonth(int month) {
        return switch (month) {
            case 3, 4, 5 -> SPRING;
            case 6, 7, 8 -> SUMMER;
            case 9, 10, 11 -> AUTUMN;
            default -> WINTER; // 12, 1, 2
        };
    }
}