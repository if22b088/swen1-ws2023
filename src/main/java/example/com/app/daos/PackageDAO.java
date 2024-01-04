package example.com.app.daos;

import example.com.app.models.User;
import example.com.app.models.Card;
import example.com.app.models.Package;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO  {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    User singleUserCache;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Package singlePackageCache;


    public PackageDAO(Connection connection) {setConnection(connection);}

    public int createPackage(List<Card> cards) {
        try {
            // disable autocommit to performa a transaction
            getConnection().setAutoCommit(false);

            //create the cards in cards table
            String insertStmt = "INSERT INTO cards (cardID, cardName, Damage) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt);

            //create a new entry in cards table for each card in the pack
            for (Card card : cards) {
                preparedStatement.setString(1, card.getCardID());
                preparedStatement.setString(2, card.getCardName());
                preparedStatement.setInt(3, card.getDamage());

                preparedStatement.executeUpdate();
            }

            //create the package by creating a new entry in database packages
            insertStmt = "INSERT INTO packages (card1 ,card2 ,card3 ,card4 ,card5) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = getConnection().prepareStatement(insertStmt);

            int parameterIndex = 1;
            for (Card card : cards) {
                preparedStatement.setString(parameterIndex, card.getCardID());
                parameterIndex++;
            }

            preparedStatement.executeUpdate();
            //commit transaction
            getConnection().commit();
            //getConnection().close();
            return 201;
        }  catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    public Boolean checkIfPackageExists() {
        try {
            String selectStmt = "SELECT packageID,card1,card2,card3,card4,card5 FROM packages LIMIT ?";

           PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, 1);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    singlePackageCache = new Package(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6));
                }
            }
            if (singlePackageCache != null) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    //todo finish buyPackage
    public String[] buyPackage(User user) {

        try {
            // disable autocommit to performa a transaction
            getConnection().setAutoCommit(false);

            //remove 5 coins from user
            String updateStmt = "UPDATE users SET coins = ? WHERE username = ?;";
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setInt(1, user.getCoins() - 5);
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.executeUpdate();


            // get one package from package table
            String selectStmt = "SELECT packageID,card1,card2,card3,card4,card5 FROM packages LIMIT ?";

            preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, 1);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    singlePackageCache = new Package(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6));
                }
            }

            // delete the package from package table
            String deleteStmt = "DELETE FROM packages WHERE packageID = ?";

            preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setInt(1, singlePackageCache.getPackageID());
            preparedStatement.executeUpdate();


            //add the cards to the stack of the user
            String insertStmt = "INSERT INTO stacks (userID, cardID) VALUES (?, ?)";
            preparedStatement = getConnection().prepareStatement(insertStmt);

            String[] cardsTmp = {singlePackageCache.getCard1(), singlePackageCache.getCard2(),
                    singlePackageCache.getCard3(),singlePackageCache.getCard4(),
                    singlePackageCache.getCard5()};

            for (String cardID : cardsTmp) {
                preparedStatement.setInt(1, user.getUserID());
                preparedStatement.setString(2, cardID);

                preparedStatement.executeUpdate();
            }

            //commit transaction
            getConnection().commit();
            //getConnection().close();

            //return array of cardIDs
            return cardsTmp;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
