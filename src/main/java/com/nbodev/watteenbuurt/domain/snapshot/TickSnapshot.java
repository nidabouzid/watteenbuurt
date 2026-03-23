package com.nbodev.watteenbuurt.domain.snapshot;

import java.time.LocalDateTime;

/**
 * Immutable record of neighbourhood state at one simulated minute.
 * Stored in the HistoryBuffer for the 24h chart.
 */
public record TickSnapshot(
        LocalDateTime time,
        double totalPowerKw,     // net neighbourhood power (kW)
        double temperatureCelsius,
        double cloudinessFactor
) {
}