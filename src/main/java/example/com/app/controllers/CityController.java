package example.com.app.controllers;

import example.com.app.models.City;
import example.com.app.repositories.CityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import example.com.server.Response;

import java.util.List;

public class CityController extends Controller {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        setCityRepository(cityRepository);
    }

    // DELETE /cities/:id -> löscht eine city mit der id
    // POST /cities -> erstellt eine neue city
    // PUT/PATCH /cities/:id -> updated eine city mit der id
    // GET /cities/:id -> die eine city zurück mit der id
    // GET /cities -> alle cities zurück
    public Response getCities() {
        try {
            List cityData = getCityRepository().getAll();
            String cityDataJSON = getObjectMapper().writeValueAsString(cityData);

            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + cityDataJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

    // GET /cities/:id
    public void getCityById(int id) {

    }

    // POST /cities
    public Response createCity(String body) {
        try {
            System.out.println(body);
            City newCity = getObjectMapper().readValue(body, City.class);
            getCityRepository().add(newCity);
            return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
        "{ \"data\": " + body + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

    // DELETE /cities/:id
    public void deleteCity(int id) {

    }

    void showCaseTest() {
        // A - arrange
        int a = 1;
        int b = 2;
        int expectedResult = 3;

        // A - act
        int actualResult = a + b;

        // A - assert
        if (expectedResult == actualResult) {
            System.out.println("Test succesful");
            return;
        }

        System.out.println("Test failed");
    }
}
