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


    public boolean addUser(User user) { return getUserDAO().create(user); }

    //used for get /users{username}
    public User getUser(String username, String token) { return getUserDAO().getUser(username, token); }

    public User getUserByToken(String token) {return getUserDAO().getUserByToken(token); }

    public void updateUser(User user, String token) { getUserDAO().updateUser(user); }

    public String loginUser(User user) { return getUserDAO().login(user); }

    // public void remove(User type) {}
}
