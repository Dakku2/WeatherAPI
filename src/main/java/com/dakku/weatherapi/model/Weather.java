package com.dakku.weatherapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "weather")
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String city;
    private String country;
    private Double temp;
    private Double feels_like;
    @Column(name = "min_temp")
    private Double temp_min;
    @Column(name = "max_temp")
    private Double temp_max;
    private Integer humidity;
    private String weather_desc;
    private String weather_icon;
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
    }
}
