package com.dakku.weatherapi.services;

import com.dakku.weatherapi.model.Weather;
import com.dakku.weatherapi.repository.WeatherRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class WeatherService {
    private static final String API_KEY = "da0f9c8d90bde7e619c3ec47766a42f4";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";

    private final WeatherRepo weatherRepo;

    // Constructor to access and manipulate data in the db
    public WeatherService(WeatherRepo weatherRepo) {
        this.weatherRepo = weatherRepo;
    }

    // Method that takes in an input of a city name and returns current weather data for that city
    public Weather getWeatherForCity(String city) throws IOException {
        Optional<Weather> optionalWeather = weatherRepo.findByCity(city);

        // If weather data is present in the db and less than 1 hr old, returns the stored weather data
        // Otherwise, it will fetch a new data from OpenWeather API
        if (optionalWeather.isPresent()) {
            Weather weather = optionalWeather.get();
            LocalDateTime lastUpdated = weather.getLastUpdated();
            LocalDateTime currentTime = LocalDateTime.now();
            long hoursSinceLastUpdate = ChronoUnit.HOURS.between(lastUpdated, currentTime);

            if (hoursSinceLastUpdate < 1) {
                // Weather data is up-to-date, no need to fetch from OpenWeather API
                return weather;
            }
        }

        // Weather data is not available or is stale, fetch from OpenWeather API
        // Constructs API URL by formatting API_URL, city(name), and API_KEY, then opens a HTTP connection to the url using HttpURLConnection
        String apiUrl = String.format(API_URL, city, API_KEY);
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Reads the response from the API endpoint using a BufferedReader and constructs a StringBuilder to store the response content
        // After entire response has been read, connection is disconnected
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder responseContent = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        reader.close();

        connection.disconnect();

        // Parsing JSON response from the API, and reads the JSON content from the responseContent with readTree() calle on the mapper obj
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = mapper.readTree(responseContent.toString());

        // at() method called on responseJson to select a specific node in the Json doc using JSON Pointer expression
        // Converted to the appropriate Java types using asText(), asDouble(), asInt() methods, returns value of JSON node as a string, double, int
        String country = responseJson.at("/sys/country").asText();
        Double temperature = responseJson.at("/main/temp").asDouble();
        Double feelsLikeTemperature = responseJson.at("/main/feels_like").asDouble();
        Double minTemperature = responseJson.at("/main/temp_min").asDouble();
        Double maxTemperature = responseJson.at("/main/temp_max").asDouble();
        Integer humidity = responseJson.at("/main/humidity").asInt();
        String weatherDescription = responseJson.at("/weather/0/description").asText();
        String weatherIcon = responseJson.at("/weather/0/icon").asText();

        // Weather obj created and populated with parsed data using setter methods, then saved to the db
        Weather weather = new Weather();
        weather.setCity(city);
        weather.setCountry(country);
        weather.setTemp(temperature);
        weather.setFeels_like(feelsLikeTemperature);
        weather.setTemp_min(minTemperature);
        weather.setTemp_max(maxTemperature);
        weather.setHumidity(humidity);
        weather.setWeather_desc(weatherDescription);
        weather.setWeather_icon(weatherIcon);

        return weatherRepo.save(weather);
    }
}
