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

    //used for /scoreboard
    public ArrayList<User> getAllUsers() { return getUserDAO().getAllUsers(); }

    //used for get /users{username}
    public User getUser(String username, String token) { return getUserDAO().getUserByUsernameToken(username, token); }

    public User getUserByUsername(String username) {return getUserDAO().getUserByUsername(username); }

    public User getUserByToken(String token) {return getUserDAO().getUserByToken(token); }

    public int updateUser(User user, String token) {  return getUserDAO().updateUser(user, token); }

    public String loginUser(User user) { return getUserDAO().login(user); }

    public void updateUserStats(User user) { getUserDAO().updateUserStats(user);
    }

    // public void remove(User type) {}
}
