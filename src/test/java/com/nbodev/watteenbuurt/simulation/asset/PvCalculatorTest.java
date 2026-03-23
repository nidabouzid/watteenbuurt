package com.nbodev.watteenbuurt.simulation.asset;

import com.nbodev.watteenbuurt.domain.weather.Season;
import com.nbodev.watteenbuurt.domain.weather.WeatherState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PvCalculatorTest {

    private final PvCalculator calc = new PvCalculator();

    @Test
    void outputIsZeroAtNight() {
        WeatherState night = new WeatherState(Season.WINTER, 5.0, 0.0, 0.0);
        assertThat(calc.compute(night)).isEqualTo(0.0);
    }

    @Test
    void outputIsMaxAtFullSunClearSky() {
        // irradiance=1, cloudiness=0 → effectiveSolarFactor=1.0 → 4 kWp
        WeatherState clearNoon = new WeatherState(Season.SUMMER, 22.0, 0.0, 1.0);
        assertThat(calc.compute(clearNoon)).isCloseTo(4.0, within(1e-9));
    }

    @Test
    void outputIsReducedByCloudCover() {
        // irradiance=1, cloudiness=1 → effectiveSolarFactor=0.2 → 0.8 kW
        WeatherState overcast = new WeatherState(Season.SUMMER, 18.0, 1.0, 1.0);
        assertThat(calc.compute(overcast)).isCloseTo(0.8, within(1e-9));
    }

    @Test
    void outputScalesWithIrradiance() {
        WeatherState halfSun = new WeatherState(Season.SPRING, 15.0, 0.0, 0.5);
        assertThat(calc.compute(halfSun)).isCloseTo(2.0, within(1e-9));
    }

    @Test
    void outputIsAlwaysNonNegative() {
        WeatherState night = new WeatherState(Season.WINTER, 2.0, 1.0, 0.0);
        assertThat(calc.compute(night)).isGreaterThanOrEqualTo(0.0);
    }
}
