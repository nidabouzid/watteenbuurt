package com.nbodev.watteenbuurt.simulation.weather;


import com.nbodev.watteenbuurt.domain.weather.Season;
import com.nbodev.watteenbuurt.domain.weather.WeatherState;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Produces deterministic weather from simulated time + a fixed seed.
 * <p>
 * Temperature model:
 * Base temp follows a seasonal sine: winter ~2°C, summer ~22°C.
 * Daily swing: ±4°C (cooler at night, warmer at midday).
 * <p>
 * Cloudiness:
 * Seeded random per simulated day — reproducible, but varies day to day.
 * Range 0.0 (clear) to 1.0 (fully overcast).
 * <p>
 * Irradiance:
 * Sine curve between sunrise (~6h) and sunset (~20h).
 * Zero at night. Scaled by seasonal day-length factor.
 */
@Service
public class WeatherService {

    private static final double TWO_PI = 2 * Math.PI;

    private final long seed;

    public WeatherService(long seed) {
        this.seed = seed;
    }

    public WeatherState compute(LocalDateTime time) {
        Season season = Season.fromMonth(time.getMonthValue());
        double temperature = computeTemperature(time);
        double cloudiness = computeCloudiness(time);
        double irradiance = computeIrradiance(time, season);
        return new WeatherState(season, temperature, cloudiness, irradiance);
    }

    private double computeTemperature(LocalDateTime time) {
        // Seasonal component: peaks in July (month 7), troughs in January (month 1)
        double dayOfYear = time.getDayOfYear();
        double seasonalBase = 12.0 + 10.0 * Math.sin((dayOfYear - 80) / 365.0 * TWO_PI);

        // Daily swing: coldest at 4am, warmest at 2pm
        double hourFraction = (time.getHour() + time.getMinute() / 60.0);
        double dailySwing = 4.0 * Math.sin((hourFraction - 4) / 24.0 * TWO_PI);

        return seasonalBase + dailySwing;
    }

    private double computeCloudiness(LocalDateTime time) {
        // New seed each day → different cloudiness per day, but reproducible
        long daySeed = seed ^ (time.getYear() * 1000L + time.getDayOfYear());
        double base = new Random(daySeed).nextDouble(); // 0.0–1.0

        // Seasonal bias: more clouds in winter
        Season season = Season.fromMonth(time.getMonthValue());
        double bias = switch (season) {
            case WINTER -> 0.3;
            case AUTUMN -> 0.2;
            case SPRING -> 0.1;
            case SUMMER -> -0.1;
        };
        return Math.max(0.0, Math.min(1.0, base + bias));
    }

    private double computeIrradiance(LocalDateTime time, Season season) {
        double hour = time.getHour() + time.getMinute() / 60.0;

        // Seasonal day length: sunrise/sunset shift with season
        double sunrise = switch (season) {
            case SUMMER -> 5.5;
            case SPRING -> 6.5;
            case AUTUMN -> 7.0;
            case WINTER -> 8.0;
        };
        double sunset = switch (season) {
            case SUMMER -> 21.5;
            case SPRING -> 20.0;
            case AUTUMN -> 18.5;
            case WINTER -> 16.5;
        };

        if (hour < sunrise || hour > sunset) return 0.0;

        // Sine arc between sunrise and sunset — peaks at solar noon
        double dayLength = sunset - sunrise;
        return Math.sin((hour - sunrise) / dayLength * Math.PI);
    }
}