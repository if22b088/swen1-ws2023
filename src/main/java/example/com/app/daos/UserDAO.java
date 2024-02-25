package example.com.app.daos;

import example.com.app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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


    //creates user in table Users
    public boolean create(User user) {
        String insertStmt = "INSERT INTO users (username, password, coins, elo, wins, losses) VALUES (?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, 20);
            preparedStatement.setInt(4, 100);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                return true;
            } else {
                return false;
            }
            
            //getConnection().close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<User> getAllUsers() {
        ArrayList <User> users = new ArrayList<>();
        String selectStmt = "SELECT name, elo, wins, losses FROM Users";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                User user = new User(resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4));
                users.add(user);
            }
            //sorts the Arraylist best on the elo value
            Collections.sort(users, Comparator.comparingInt(User::getElo));
            //getConnection().close();
            return users;
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //used for battlelogic
    public User getUserByUsername(String userName) {
        singleUserCache= null;
        //query requested user
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

            
            //getConnection().close();
            return singleUserCache;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByUsernameToken(String userName, String token) {
        singleUserCache= null;
        //query requested user
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
            //query admin token
            selectStmt = "SELECT userID, username, name, token, bio, image, coins, elo, wins, losses FROM users WHERE token = ?;";
            preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, token);
            User adminUserCache = new User();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    adminUserCache = new User(resultSet.getInt(1),resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4),
                            resultSet.getString(5), resultSet.getString(6),
                            resultSet.getInt(7), resultSet.getInt(8),
                            resultSet.getInt(9), resultSet.getInt(10));
                }
            }

            //if token is not equal user- or admin-token return null

            if (singleUserCache == null || !singleUserCache.getToken().equals(token) || !adminUserCache.getToken().equals(token) ) {
                return null;
            }
            
            //getConnection().close();
           return singleUserCache;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public User getUserByToken(String token) {
        singleUserCache= null;
        String selectStmt = "SELECT userID, username, name, token, bio, image, coins, elo, wins, losses FROM users WHERE token = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, token);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    singleUserCache = new User(resultSet.getInt(1),resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4),
                            resultSet.getString(5), resultSet.getString(6),
                            resultSet.getInt(7), resultSet.getInt(8),
                            resultSet.getInt(9), resultSet.getInt(10));
                }
            }
            if (!singleUserCache.getToken().equals( token)) {
                return null;
            }
            
            //getConnection().close();
            return singleUserCache;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int updateUser(User user, String token) {

        //query admin token
        String selectStmt = "SELECT userID, username, name, token, bio, image, coins, elo, wins, losses FROM users WHERE token = ?;";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt)) {
            preparedStatement.setString(1, token);
            User adminUserCache = new User();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    adminUserCache = new User(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4),
                            resultSet.getString(5), resultSet.getString(6),
                            resultSet.getInt(7), resultSet.getInt(8),
                            resultSet.getInt(9), resultSet.getInt(10));
                }
            }
            //if token does not match the user to be updated or the admin token return 401 (forbidden)
            String selectStmt2 = "SELECT token FROM users WHERE username = ?;";
            PreparedStatement preparedStatement1 = getConnection().prepareStatement(selectStmt2);
            preparedStatement1.setString(1, user.getUsername());
            User userCache = new User();
            try (ResultSet resultSet = preparedStatement1.executeQuery()) {
                if (resultSet.next()) {
                     userCache = new User(resultSet.getString(1));
                }
            }
            user.setToken(userCache.getToken());
            if(user.getToken()!= null) {

                if (!user.getToken().equals(token) || !adminUserCache.getToken().equals(token)) {
                    return 401;
                }
            } else {
                return 404;
            }

            //update the user
            String updateStmt = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?;";
            try {
                PreparedStatement preparedStatement2 = getConnection().prepareStatement(updateStmt);
                preparedStatement2.setString(1, user.getName());
                preparedStatement2.setString(2, user.getBio());
                preparedStatement2.setString(3, user.getImage());
                preparedStatement2.setString(4, user.getUsername());
                int updated = preparedStatement2.executeUpdate();

                if (updated > 0) {
                    return 200;
                } else {
                    return 404;
                }
                
                //getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /*
    public void removeCoin(User user) {

        String updateStmt = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?;";
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getBio());
            preparedStatement.setString(3, user.getImage());
            preparedStatement.setString(4, user.getUsername());
            preparedStatement.executeUpdate();
            
            //getConnection().close();
            //setCitiesCache(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
    public String login(User user) {
        singleUserCache= null;
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
            if (singleUserCache != null) {
                if (singleUserCache.getPassword().equals(user.getPassword())) {

                    String token = user.getUsername() + "-mtcgToken";
                    String updateStmt = "UPDATE Users SET token = ? WHERE username = ?";

                    try {
                        PreparedStatement preparedStatement2 = getConnection().prepareStatement(updateStmt);
                        preparedStatement2.setString(1, token);
                        preparedStatement2.setString(2, user.getUsername());
                        preparedStatement2.executeUpdate();

                        //getConnection().close();

                        return token;

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }


            //getConnection().close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    public void updateUserStats(User user) {

        //query admin token
        String updateStmt = "UPDATE users SET elo = ?, wins = ?, losses= ? WHERE username = ?;";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt)) {

            //update the user
            try {

                preparedStatement.setInt(1, user.getElo());
                preparedStatement.setInt(2, user.getWins());
                preparedStatement.setInt(3, user.getLosses());
                preparedStatement.setString(4, user.getUsername());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteToken(String token) {
        String updateStmt = "UPDATE Users SET Token = NULL WHERE Token = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt)) {

            //update the user
            try {
                preparedStatement.setString(1, token);
                int result = preparedStatement.executeUpdate();
                getConnection().commit();

                if (result > 0) {
                    return true;
                } else {
                    return false;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
