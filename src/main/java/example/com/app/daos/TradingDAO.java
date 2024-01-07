package example.com.app.daos;

import example.com.app.models.Card;
import example.com.app.models.User;
import example.com.app.models.Trading;
import example.com.app.models.Package;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TradingDAO  {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;


    public TradingDAO(Connection connection) {setConnection(connection);}


    //gets all the trading deals from the DB table Tradings
    public ArrayList <Trading> getTradings() {
        ArrayList <Trading> tradings = new ArrayList();
        String selectStmt = "SELECT UsernameOfferer, TradingID, CardToTrade, CardType, MinimumDamage FROM Tradings;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Trading trading = new Trading(resultSet.getString(1),resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5));
                tradings.add(trading);
            }
            //getConnection().close();
            return tradings;
        }


        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //creates a new trade deal in DB table Tradings
    public int createTrading(Trading newTrading, User user) {
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //check if user owns one of the cards (i.e. is in his stack)
        String selectStmt = "SELECT c.CardID FROM Cards c " +
                "JOIN Stacks s ON c.CardID = s.CardID " +
                "JOIN Users u ON s.UserID = u.UserID " +
                "WHERE u.Username = ?;";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<String> userCardIDs = new ArrayList<>();
            while(resultSet.next()) {
                userCardIDs.add(resultSet.getString(1));
            }

            boolean found = false;
            for (String userCardID : userCardIDs) {
                if (userCardID.equals(newTrading.getCardToTrade())) {
                    found = true;
                    break;
                }
            }
            //if card is not in the users stack (i.e. the user does not own the card return FORBIDDEN error code)
            if (!found) {
                return 403;
            }

            //check if card is in the users deck (and therefore loocked)
            selectStmt = "SELECT Decks.Card1 , Decks.Card2, Decks.Card3, Decks.Card4 FROM Decks JOIN Users ON Decks.DeckID = Users.DeckID WHERE Users.Username = ?;";
            preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, user.getUsername());
            resultSet = preparedStatement.executeQuery();

            ArrayList<String> userDeck = new ArrayList<>();
            while(resultSet.next()) {
                userDeck.add(resultSet.getString(1));
            }

            //if card is in the deck (and therefore locked) return FORBIDDEN error code
            for (String deckCardID : userDeck) {
                if (deckCardID.equals(newTrading.getCardToTrade())) {
                    return 403;
                }
            }

            //check if deal with this id already exists return CONFLICT error code
            ArrayList<Trading> existingTradings = getTradings();
            for (Trading trading : existingTradings) {
                if (trading.getId().equals(newTrading.getId())) {
                    return 409;
                }
            }

            //create tradedeal

            String insertStmt = "INSERT INTO tradings (UsernameOfferer,TradingID, CardToTrade, CardType, MinimumDamage) VALUES (?, ?, ?, ?, ?);";
            preparedStatement = getConnection().prepareStatement(insertStmt);

            //create a new entry in cards table for each card in the pack
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, newTrading.getId());
            preparedStatement.setString(3, newTrading.getCardToTrade());
            preparedStatement.setString(4, newTrading.getCardToTrade());
            preparedStatement.setInt(5, newTrading.getMinDamage());



            int result = preparedStatement.executeUpdate();
            //commit transaction
            getConnection().commit();
            if (result > 0) {
                return 201;
            } else {
                return 0;
            }


            //getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteTrading(String tradingDealID, String token) {

        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //check if trading deal exists
        try {
            String selectStmt = "SELECT UsernameOfferer, TradingID, CardToTrade, CardType, MinimumDamage FROM Tradings WHERE TradingID = ?;";
            Trading tradeDeal;
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, tradingDealID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                tradeDeal = new Trading(resultSet.getString(1),resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5));
            //if no trading was found with the tradingDealID return NOT FOUND error code
            } else {
                return 404;
            }

            //check if user owns one of the cards (i.e. is in his stack)
            selectStmt = "SELECT c.CardID FROM Cards c " +
                    "JOIN Stacks s ON c.CardID = s.CardID " +
                    "JOIN Users u ON s.UserID = u.UserID " +
                    "WHERE u.Token = ?;";


            preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, token);
            resultSet = preparedStatement.executeQuery();

            ArrayList<String> userCardIDs = new ArrayList<>();
            while(resultSet.next()) {
                userCardIDs.add(resultSet.getString(1));
            }

            boolean found = false;
            for (String userCardID : userCardIDs) {
                if (userCardID.equals(tradeDeal.getCardToTrade())) {
                    found = true;
                    break;
                }
            }
            //if card is not in the users stack (i.e. the user does not own the card return FORBIDDEN error code)
            if (!found) {
                return 403;
            }

            //delete the trading deal
            String deleteStmt = "DELETE FROM Tradings WHERE TradingID = ?;";

            preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setString(1, tradingDealID);
            int result = preparedStatement.executeUpdate();
            getConnection().commit();

            if (result > 0) {
                return 200;
            }
            getConnection().commit();

            //getConnection().close();
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 400;
    }

    public int carryOutTrading(Card cardToTrade, String tradingDealID, User user) {
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //check if trading deal exists
        try {
            String selectStmt = "SELECT UsernameOfferer, TradingID, CardToTrade, CardType, MinimumDamage FROM Tradings WHERE TradingID = ?;";
            Trading tradeDeal;
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, tradingDealID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                tradeDeal = new Trading(resultSet.getString(1),resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5));
                //if no trading was found with the tradingDealID return NOT FOUND error code
            } else {
                return 404;
            }

            //check if user owns the card (i.e. is in his stack)
            selectStmt = "SELECT c.CardID FROM Cards c " +
                    "JOIN Stacks s ON c.CardID = s.CardID " +
                    "JOIN Users u ON s.UserID = u.UserID " +
                    "WHERE u.Token = ?;";


            preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, user.getToken());
            resultSet = preparedStatement.executeQuery();

            ArrayList<String> userCardIDs = new ArrayList<>();
            while(resultSet.next()) {
                userCardIDs.add(resultSet.getString(1));
            }

            boolean found = false;
            for (String userCardID : userCardIDs) {
                if (userCardID.equals(tradeDeal.getCardToTrade())) {
                    found = true;
                    break;
                }
            }
            //if card is not in the users stack (i.e. the user does not own the card return FORBIDDEN error code)
            if (!found) {
                return 403;
            }

            //check if the offered card meets the trade deal requirments (minDamage and cardType)
            if (cardToTrade.getDamage() < tradeDeal.getMinDamage() || !cardToTrade.getCardType().equals( tradeDeal.getCardType())) {
                return 403;
            }

            //carry out the trade deal:

            //delete the card from user who accepted the trading deal (the user who sent the request)
            String deleteStmt = "DELETE FROM Stacks WHERE Username = ? AND CardID = ?;";

            preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, cardToTrade.getCardID());
            int result = preparedStatement.executeUpdate();
            getConnection().commit();

            if (result > 0) {
                return 200;
            }

            //add the card to the user who created the trading deal (offerer)
            String insertStmt = "INSERT INTO Stacks (UserID, CardID) VALUES (?, ?);";

            preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, tradeDeal.getUsernameOfferer());
            preparedStatement.setString(2, cardToTrade.getCardID());
            result = preparedStatement.executeUpdate();
            getConnection().commit();

            if (result > 0) {
                return 200;
            }

            //add the card to user who accepted the trading deal (the user who sent the request)
            insertStmt = "INSERT INTO Stacks (UserID, CardID) VALUES (?, ?);";

            preparedStatement = getConnection().prepareStatement(insertStmt);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, tradeDeal.getCardToTrade());
            result = preparedStatement.executeUpdate();
            getConnection().commit();

            if (result > 0) {
                return 200;
            }

            //delete the card to the user who created the trading deal (offerer)
            deleteStmt = "DELETE FROM Stacks WHERE Username = ? AND CardID = ?;";

            preparedStatement = getConnection().prepareStatement(deleteStmt);
            preparedStatement.setString(1, tradeDeal.getUsernameOfferer());
            preparedStatement.setString(2, tradeDeal.getCardToTrade());
            result = preparedStatement.executeUpdate();
            getConnection().commit();

            if (result > 0) {
                return 200;
            }

            getConnection().commit();

            //getConnection().close();
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 400;
    }

}
