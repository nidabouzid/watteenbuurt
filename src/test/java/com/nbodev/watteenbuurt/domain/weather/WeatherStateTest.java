package com.nbodev.watteenbuurt.domain.weather;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class WeatherStateTest {

    @Test
    void effectiveSolarFactorIsZeroAtNight() {
        WeatherState night = new WeatherState(Season.WINTER, 5.0, 0.0, 0.0);
        assertThat(night.effectiveSolarFactor()).isEqualTo(0.0);
    }

    @Test
    void effectiveSolarFactorIsMaxAtFullSunClearSky() {
        WeatherState clearNoon = new WeatherState(Season.SUMMER, 22.0, 0.0, 1.0);
        assertThat(clearNoon.effectiveSolarFactor()).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void effectiveSolarFactorReducedByCloudCover() {
        // irradiance=1, cloudiness=1 → 1 * (1 - 0.8) = 0.2
        WeatherState overcast = new WeatherState(Season.SUMMER, 18.0, 1.0, 1.0);
        assertThat(overcast.effectiveSolarFactor()).isCloseTo(0.2, within(1e-9));
    }

    @Test
    void effectiveSolarFactorWithPartialCloud() {
        // irradiance=0.8, cloudiness=0.5 → 0.8 * (1 - 0.4) = 0.48
        WeatherState partial = new WeatherState(Season.SPRING, 15.0, 0.5, 0.8);
        assertThat(partial.effectiveSolarFactor()).isCloseTo(0.48, within(1e-9));
    }
}
