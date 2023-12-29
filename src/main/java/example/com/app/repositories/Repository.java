package example.com.app.repositories;

import java.util.ArrayList;

public interface Repository<T> {
    ArrayList<T> getAll();
    //T get(int id);
    void add(T type);
    void update(T type);
    void remove(T type);
}
