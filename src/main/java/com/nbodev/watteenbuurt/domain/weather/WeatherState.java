package com.nbodev.watteenbuurt.domain.weather;

/**
 * Deterministic snapshot of weather at a point in simulated time.
 * All values are computed from simulated date/time — no external API needed.
 */
public record WeatherState(
        Season season,
        double temperatureCelsius,   // ambient air temperature
        double cloudinessFactor,     // 0.0 (clear) to 1.0 (fully overcast)
        double irradianceFactor      // 0.0 (night/overcast) to 1.0 (peak sun)
) {
    /**
     * Effective solar production factor combining time-of-day and cloud cover.
     */
    public double effectiveSolarFactor() {
        return irradianceFactor * (1.0 - cloudinessFactor * 0.8);
    }
}