package com.nbodev.watteenbuurt.api.controller;


import com.nbodev.watteenbuurt.api.dto.SimulationStateDto;
import com.nbodev.watteenbuurt.api.dto.SnapshotDto;
import com.nbodev.watteenbuurt.domain.weather.WeatherState;
import com.nbodev.watteenbuurt.simulation.SimulationClock;
import com.nbodev.watteenbuurt.simulation.SimulationEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationEngine engine;

    public SimulationController(SimulationEngine engine) {
        this.engine = engine;
    }

    /**
     * GET /api/simulation/state
     * Current clock, weather, and aggregate neighbourhood power.
     * Polled by the UI every second.
     */
    @GetMapping("/state")
    public SimulationStateDto getState() {
        SimulationClock clock = engine.getClock();
        WeatherState weather = engine.getCurrentWeather();

        return new SimulationStateDto(
                clock.getSimulatedTime(),
                weather == null ? "UNKNOWN" : weather.season().name(),
                weather == null ? 0 : weather.temperatureCelsius(),
                weather == null ? 0 : weather.cloudinessFactor(),
                weather == null ? 0 : weather.irradianceFactor(),
                engine.getNeighbourhood().getTotalCurrentPowerKw(),
                clock.isRunning()
        );
    }

    /**
     * GET /api/simulation/history
     * Last 24 simulated hours of neighbourhood snapshots for the chart.
     */
    @GetMapping("/history")
    public List<SnapshotDto> getHistory() {
        return engine.getHistoryBuffer().getAll().stream()
                .map(s -> new SnapshotDto(
                        s.time(),
                        s.totalPowerKw(),
                        s.temperatureCelsius(),
                        s.cloudinessFactor()))
                .toList();
    }

    /**
     * POST /api/simulation/control
     * Body: { "action": "pause" | "resume" }
     */
    @PostMapping("/control")
    public ResponseEntity<Map<String, String>> control(@RequestBody Map<String, String> body) {
        String action = body.getOrDefault("action", "");
        SimulationClock clock = engine.getClock();

        switch (action) {
            case "pause" -> clock.stop();
            case "resume" -> clock.start();
            default -> {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unknown action: " + action));
            }
        }

        return ResponseEntity.ok(Map.of("status", action + "d", "running",
                String.valueOf(clock.isRunning())));
    }
}