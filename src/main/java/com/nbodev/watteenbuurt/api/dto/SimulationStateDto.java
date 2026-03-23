package com.nbodev.watteenbuurt.api.dto;

import java.time.LocalDateTime;

public record SimulationStateDto(
        LocalDateTime simulatedTime,
        String season,
        double temperatureCelsius,
        double cloudinessFactor,
        double irradianceFactor,
        double currentTotalPowerKw,
        boolean running
) {
}