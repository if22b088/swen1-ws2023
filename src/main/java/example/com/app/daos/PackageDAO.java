package example.com.app.daos;

import example.com.app.models.User;
import example.com.app.models.Package;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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


    public PackageDAO(Connection connection) {setConnection(connection);
    }


    //todo finish buyPackage
    public boolean buyPackage(User user) {

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
            String selectStmt = "SELECT cardID,card1,card2,card3,card4,card5 FROM packages LIMIT ?";

            preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, 1);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                singlePackageCache = new Package(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6));
            }

            // delete the package from package table
            String deleteStmt = "DELETE FROM packages WHERE packageID = ?";

            preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setString(1, singlePackageCache.getCardID());
            preparedStatement.executeUpdate();


            //add the cards to the stack of the user
            String insertStmt = "INSERT INTO stacks (userID, cardID) VALUES (?, ?)";
            preparedStatement = getConnection().prepareStatement(insertStmt);

            String[] cardsTmp = {singlePackageCache.getCard1(), singlePackageCache.getCard2(),
                    singlePackageCache.getCard3(),singlePackageCache.getCard4(),
                    singlePackageCache.getCard5()};


            for (String cardID : cardsTmp) {
                // Set parameters for the prepared statement
                preparedStatement.setInt(1, user.getUserID());
                preparedStatement.setString(2, cardID);

                // Execute the query
                preparedStatement.executeUpdate();
            }




            //commit transaction
            getConnection().commit();

            getConnection().close();


            //setCitiesCache(null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return false;
    }

}
