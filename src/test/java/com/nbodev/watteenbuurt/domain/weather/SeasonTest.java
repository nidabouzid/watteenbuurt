package com.nbodev.watteenbuurt.domain.weather;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class SeasonTest {

    @ParameterizedTest(name = "month {0} -> {1}")
    @CsvSource({
            "1,  WINTER",
            "2,  WINTER",
            "12, WINTER",
            "3,  SPRING",
            "4,  SPRING",
            "5,  SPRING",
            "6,  SUMMER",
            "7,  SUMMER",
            "8,  SUMMER",
            "9,  AUTUMN",
            "10, AUTUMN",
            "11, AUTUMN",
    })
    void fromMonthMapsCorrectly(int month, Season expected) {
        assertThat(Season.fromMonth(month)).isEqualTo(expected);
    }
}
