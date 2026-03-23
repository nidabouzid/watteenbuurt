package com.nbodev.watteenbuurt.api.dto;

public record NeighbourhoodSummaryDto(
        int totalHouses,
        int housesWithPv,
        int housesWithHeatPump,
        int housesWithHomeEv,
        int publicChargers,
        double currentTotalPowerKw
) {
}