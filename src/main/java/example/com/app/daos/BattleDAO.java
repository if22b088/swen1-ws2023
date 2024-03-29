package example.com.app.daos;


import example.com.app.models.User;
import example.com.app.models.Battle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class BattleDAO {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Battle battleCache;


    public BattleDAO(Connection connection) { setConnection(connection); }


    //checks if battles exist that don't have a second user and where user1 != user parameter
    public Battle getPendingBattles(User user) {

        String selectStmt = "SELECT * FROM Battles WHERE User2 IS NULL AND User1 != NULL AND User1 != ?";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, user.getUsername());

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                battleCache = new Battle(resultSet.getInt(1),resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4));
            }
            //getConnection().close();
            return battleCache;
        }

        catch (SQLException e) {
            e.printStackTrace();
            battleCache.setBattleID(-1);
            return battleCache;
        }
    }

    public int createBattle(User user) {
        int newBattleID = 0;
        try {
            String insertStmt = "INSERT INTO Battles (User1) VALUES (?)";
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);

            preparedStatement.setString(1, user.getUsername());

            int result = preparedStatement.executeUpdate();
            newBattleID = -1;

            if (result > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    newBattleID = resultSet.getInt("battleID");
                }
            }

            //getConnection().close();
            return newBattleID;
        } catch (SQLException e) {
            return newBattleID;
        }
    }

    //checks if battle with battleID exists and if user2 and the log are not null
    public Boolean checkIfBattleComplete(int battleID) {

        String selectStmt = "SELECT BattleID FROM Battles WHERE BattleID = ? AND User2 IS NOT NULL AND battleLog IS NOT NULL;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, battleID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
            //getConnection().close();
        }

        catch (SQLException e) {
            return false;
        }
        return false;
    }

    //returns BattleLog Battle with specific battleID
    public String getBattleLog(int battleID) {

        String selectStmt = "SELECT BattleLog FROM Battles WHERE BattleID = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, battleID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String battleLog = resultSet.getString("BattleLog");
                return battleLog;
            }
            //getConnection().close();
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void updateBattleLog(int battleID, String battleLog) {

        String updateStmt = "UPDATE battles SET battleLog = ? WHERE battleID = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, battleLog);
            preparedStatement.setInt(2, battleID);

            preparedStatement.executeUpdate();
            //getConnection().close();
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}