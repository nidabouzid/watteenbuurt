package com.nbodev.watteenbuurt.api.dto;

public record PublicChargerDto(
        String id,
        double currentPowerKw,
        double totalKwh,
        boolean charging
) {
}