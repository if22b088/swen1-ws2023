package example.com.app.daos;

import example.com.app.models.City;

import java.util.ArrayList;

public interface DAO<T> {
    //void create(T user);

    //void create(T city);

    //ArrayList<T> readAll();
    //T read(int id);
    T read(String username);
    void update(T type);
    void delete(int id);
}
