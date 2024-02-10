package example.com.app.daos;

import example.com.app.models.Card;
import example.com.app.models.User;
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


    public BattleDAO(Connection connection) { setConnection(connection); }

    public int getPendingBattles(User user) {
        int battleID = -1;
        String selectStmt = "SELECT BattleID FROM Battles WHERE User2 IS NULL AND User1 != ?";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, user.getUsername());

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                battleID = resultSet.getInt("BattleID");
            }
            //getConnection().close();
            return battleID;
        }


        catch (SQLException e) {
            e.printStackTrace();
        }
        return battleID;
    }
    public int createBattle(User user) {
        try {
            String insertStmt = "INSERT INTO Battles (User1) VALUES (?)";
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);

            preparedStatement.setString(1, user.getUsername());

            int result = preparedStatement.executeUpdate();
            int newBattleID = -1;

            if (result >0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    newBattleID = resultSet.getInt("battleID");
                }
            }

            //getConnection().close();
            return newBattleID;
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //checks if battle with battleID exists and if user2 is not null
    public Boolean checkIfBattleComplete(int battleID) {

        String selectStmt = "SELECT BattleID FROM Battles WHERE BattleID = ? AND User2 IS NOT NULL;";

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
            e.printStackTrace();
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

}