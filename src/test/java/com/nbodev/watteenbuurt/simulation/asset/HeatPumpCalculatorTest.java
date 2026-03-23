package com.nbodev.watteenbuurt.simulation.asset;

import com.nbodev.watteenbuurt.domain.weather.Season;
import com.nbodev.watteenbuurt.domain.weather.WeatherState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HeatPumpCalculatorTest {

    private final HeatPumpCalculator calc = new HeatPumpCalculator();

    private WeatherState weather(double temp) {
        return new WeatherState(Season.WINTER, temp, 0.5, 0.0);
    }

    @Test
    void isOffAtComfortThreshold() {
        assertThat(calc.compute(weather(18.0))).isEqualTo(0.0);
    }

    @Test
    void isOffAboveComfortThreshold() {
        assertThat(calc.compute(weather(20.0))).isEqualTo(0.0);
        assertThat(calc.compute(weather(25.0))).isEqualTo(0.0);
    }

    @Test
    void isAtMaxPowerAtMinusFiveCelsius() {
        // delta = 18 - (-5) = 23 = TEMP_RANGE → clamped to 1.0 → 3.5 kW
        assertThat(calc.compute(weather(-5.0))).isCloseTo(3.5, within(1e-9));
    }

    @Test
    void isAtMaxPowerBelowMinusFive() {
        // delta > TEMP_RANGE → still clamped at 3.5 kW
        assertThat(calc.compute(weather(-10.0))).isCloseTo(3.5, within(1e-9));
    }

    @Test
    void scalesLinearlyBelowThreshold() {
        // delta = 18 - 0 = 18, range = 23 → 3.5 * (18/23)
        double expected = 3.5 * (18.0 / 23.0);
        assertThat(calc.compute(weather(0.0))).isCloseTo(expected, within(1e-9));
    }

    @Test
    void powerIncreasesAsTempDrops() {
        double powerAt10 = calc.compute(weather(10.0));
        double powerAt5 = calc.compute(weather(5.0));
        double powerAt0 = calc.compute(weather(0.0));

        assertThat(powerAt5).isGreaterThan(powerAt10);
        assertThat(powerAt0).isGreaterThan(powerAt5);
    }
}
