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


    public void addUser(User user) { getUserDAO().create(user); }

    public User getUser(String username, String token) { return getUserDAO().read(username, token); }

    public void updateUser(User user, String token) { getUserDAO().update(user, token); }

    public String loginUser(User user) { return getUserDAO().login(user); }

    // public void remove(User type) {}
}
