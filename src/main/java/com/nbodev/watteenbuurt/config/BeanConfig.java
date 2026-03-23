package com.nbodev.watteenbuurt.config;

import com.nbodev.watteenbuurt.simulation.weather.WeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public WeatherService weatherService(SimulationConfig config) {
        return new WeatherService(config.getRandomSeed());
    }
}