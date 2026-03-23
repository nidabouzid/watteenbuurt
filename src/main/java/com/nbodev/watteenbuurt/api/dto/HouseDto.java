package com.nbodev.watteenbuurt.api.dto;

import java.util.List;

public record HouseDto(
        String id,
        double currentNetPowerKw,   // positive = consuming, negative = exporting
        double netTotalKwh,         // cumulative net energy since start
        List<AssetDto> assets
) {
}