package com.nbodev.watteenbuurt.simulation.asset;


import com.nbodev.watteenbuurt.domain.weather.WeatherState;

/**
 * PV panel generation model.
 * <p>
 * Peak output: 4 kWp (typical residential installation, ~12 panels).
 * Actual output = peakKwp × effectiveSolarFactor (irradiance × cloud reduction).
 * <p>
 * PV accounting assumption:
 * Generation first offsets house load. Any surplus exports to the grid.
 * Surplus is represented as negative net load on the house meter.
 * No battery storage modeled.
 */
public class PvCalculator {

    private static final double PEAK_KWP = 4.0;

    public double compute(WeatherState weather) {
        return PEAK_KWP * weather.effectiveSolarFactor();
    }
}