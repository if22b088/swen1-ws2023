package example.com.app.repositories;

import example.com.app.daos.CityDAO;
import example.com.app.models.City;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class CityRepository implements Repository<City> {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    CityDAO cityDAO;

    public CityRepository(CityDAO cityDAO) {
        setCityDAO(cityDAO);
    }

    @Override
    public ArrayList<City> getAll() {
        ArrayList<City> cities = getCityDAO().readAll();

        return cities;
    }


    public City get(int id) {
        return null;
    }

    @Override
    public void add(City city) {
        getCityDAO().create(city);
    }

    @Override
    public void update(City type) {

    }

    @Override
    public void remove(City type) {

    }
}
