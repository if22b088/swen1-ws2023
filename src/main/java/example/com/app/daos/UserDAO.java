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
/*
    @Setter(AccessLevel.PRIVATE)
    ArrayList<User> usersCache;
*/

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    User singleUserCache;


    public UserDAO(Connection connection) {
        setConnection(connection);
    }


    public boolean create(User user) {
        String insertStmt = "INSERT INTO users (username, password) VALUES (?, ?);";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.execute();
            //todo fix connection close
            //getConnection().close();
            return true;


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public User read(String userName, String token) {

        String selectStmt = "SELECT userID, username, name, token, bio, image, coins, elo, wins, losses FROM users WHERE username = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, userName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    singleUserCache = new User(resultSet.getInt(1),resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4),
                            resultSet.getString(5), resultSet.getString(6),
                            resultSet.getInt(7), resultSet.getInt(8),
                            resultSet.getInt(9), resultSet.getInt(10));
                }
            }
            getConnection().close();
            if (!singleUserCache.getToken().equals( token)) {
                return null;
            }
           return singleUserCache;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateUser(User user) {

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


    public void removeCoin(User user) {

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

    public String login(User user) {
        String selectStmt = "SELECT username, password FROM users WHERE username = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, user.getUsername());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    singleUserCache = new User(resultSet.getString(1), resultSet.getString(2));
                }
            }
            //getConnection().close();
            if(singleUserCache.getPassword().equals(user.getPassword())) {

                String token = user.getUsername() + "-mtcgToken";

                String updateStmt = "UPDATE Users SET token = ? WHERE username = ?";

                try {

                    PreparedStatement preparedStatement2 = getConnection().prepareStatement(updateStmt);
                    preparedStatement2.setString(1, token);
                    preparedStatement2.setString(2, user.getUsername());
                    preparedStatement2.execute();
                    //todo fix connection close
                    //getConnection().close();

                    return token;

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

//todo fix connection close
            //getConnection().close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void delete(int id) {

    }
}
