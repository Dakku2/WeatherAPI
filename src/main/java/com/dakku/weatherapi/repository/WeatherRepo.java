package com.dakku.weatherapi.repository;

import com.dakku.weatherapi.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
// Provides WeatherRepo to provide basic CRUD operations for the entity(Weather)
public interface WeatherRepo extends JpaRepository<Weather, Long> {
    // Custom query method that retrieves a Weather entity by its city attr, returns an optional obj to check weather entity with the given city exists or not
    Optional<Weather> findByCity(String city);
}
