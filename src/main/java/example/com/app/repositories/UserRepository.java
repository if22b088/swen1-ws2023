package example.com.app.repositories;

import example.com.app.daos.UserDAO;
import example.com.app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class UserRepository {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    UserDAO userDAO;

    public UserRepository(UserDAO userDAO) { setUserDAO(userDAO); }


    public void add(User user) { getUserDAO().create(user); }

    public User get(String username) { return getUserDAO().read(username); }

    public void update(User user) { getUserDAO().update(user); }

    // public void remove(User type) {}
}
