package example.com.app.daos;

import example.com.app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO  {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    ArrayList<User> usersCache;


    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    User singleUserCache;


    public UserDAO(Connection connection) {
        setConnection(connection);
    }


    public void create(User user) {
        String insertStmt = "INSERT INTO users (name, password) VALUES (?, ?);";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.execute();
            getConnection().close();
            //setCitiesCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User read(String userName) {

        String selectStmt = "SELECT name, bio, image FROM users WHERE name = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = preparedStatement.executeQuery();
            singleUserCache = new User(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
            getConnection().close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return singleUserCache;
    }


    public void update(User user) {

        String updateStmt = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getBio());
            preparedStatement.setString(3, user.getImage());
            preparedStatement.setString(4, user.getUsername());
            preparedStatement.executeUpdate();
            getConnection().close();
            //setCitiesCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(int id) {

    }
}
