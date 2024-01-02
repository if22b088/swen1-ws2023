package example.com.app.daos;

import example.com.app.models.City;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CityDAO {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<City> citiesCache;

    public  CityDAO(Connection connection) {
        setConnection(connection);
    }


    public void create(City city) {
        String insertStmt = "INSERT into cities (name, population) VALUES (?, ?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, city.getName());
            preparedStatement.setInt(2, city.getPopulation());
            preparedStatement.execute();
            getConnection().close();
            setCitiesCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<City> readAll() {
        ArrayList<City> cities = new ArrayList();

        if (citiesCache != null) {
            System.out.println("TEST");
            return citiesCache;
        }

        String selectStmt = "SELECT name, population from cities;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                City city = new City(resultSet.getString(1), resultSet.getInt(2));
                cities.add(city);
            }
            setCitiesCache(cities);
            getConnection().close();
            return cities;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public City read(int id) {
        return null;
    }


    public void update(City user) {

    }


    public void delete(int id) {

    }
}
