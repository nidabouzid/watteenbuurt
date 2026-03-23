package com.nbodev.watteenbuurt.simulation.asset;


import com.nbodev.watteenbuurt.domain.weather.WeatherState;

/**
 * Heat pump consumption model.
 * <p>
 * Assumption: heat pump runs for space heating only (not cooling).
 * Power scales linearly with how far temperature is below 18°C comfort threshold.
 * At 18°C+ the heat pump is off (0 kW).
 * At -5°C it draws ~3.5 kW.
 * <p>
 * Formula: power = maxKw * max(0, (threshold - temp) / range)
 */
public class HeatPumpCalculator {

    private static final double COMFORT_THRESHOLD_C = 18.0;
    private static final double MAX_POWER_KW = 3.5;
    private static final double TEMP_RANGE = 23.0; // degrees below threshold for max power

    public double compute(WeatherState weather) {
        double delta = COMFORT_THRESHOLD_C - weather.temperatureCelsius();
        if (delta <= 0) return 0.0;
        return MAX_POWER_KW * Math.min(1.0, delta / TEMP_RANGE);
    }
}