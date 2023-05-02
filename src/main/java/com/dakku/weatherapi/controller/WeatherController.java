package com.dakku.weatherapi.controller;

import com.dakku.weatherapi.model.Weather;
import com.dakku.weatherapi.services.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;
    // Constructor to initialize WeatherController with WeatherService to retrieve weather data from WeatherService
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    public ResponseEntity<Object> getWeatherForCity(@PathVariable String city) throws IOException {
        Weather weather = weatherService.getWeatherForCity(city);
        return ResponseEntity.ok(weather);
    }
}