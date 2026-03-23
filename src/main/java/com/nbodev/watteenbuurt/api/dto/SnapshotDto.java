package com.nbodev.watteenbuurt.api.dto;

import java.time.LocalDateTime;

public record SnapshotDto(
        LocalDateTime time,
        double totalPowerKw,
        double temperatureCelsius,
        double cloudinessFactor
) {
}