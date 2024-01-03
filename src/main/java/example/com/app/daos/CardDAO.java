package example.com.app.daos;

import example.com.app.models.Card;
import example.com.app.models.Deck;

import example.com.app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CardDAO {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    Connection connection;


    @Setter(AccessLevel.PRIVATE)
    ArrayList <Card> cardsCache;


    public CardDAO(Connection connection) {
        setConnection(connection);
    }


    //gets all cards from a user that has a specific token
    public ArrayList <Card> getAllCards(String token) {
        ArrayList <Card> cards =new ArrayList();
        String selectStmt = "SELECT c.CardID, c.CardName, c.CardType c.Damage FROM Cards c " +
                "JOIN Stacks s ON c.CardID = s.CardID " +
                "JOIN Users u ON s.UserID = u.UserID " +
                "WHERE u.Token = ?";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Card card = new Card(resultSet.getString(1), resultSet.getString(2),resultSet.getString(3), resultSet.getInt(4));
                cards.add(card);
            }
            getConnection().close();
            return cards;
        }


        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //gets the deck from a user that has a specific token
    public ArrayList <String> getDeck(String token) {
        ArrayList <String> deck =new ArrayList();

        String selectStmt = "SELECT D.DeckID, D.Card1, D.Card2, D.Card3, D.Card4 " +
                "FROM Decks D " +
                "JOIN Users U ON D.DeckID = U.DeckID " +
                "WHERE U.Token = ?";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();

            for(int i = 0; i < 4; i++ ) {
                deck.add(resultSet.getString(i));
            }
            getConnection().close();
            return deck;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //get a single card based on cardID
    public Card getSingleCard(String cardID) {
        String selectStmt = "SELECT CardName, CardType, Damage" +
                "FROM Cards" +
                "WHERE cardID = ?";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, cardID);
            ResultSet resultSet = preparedStatement.executeQuery();

            Card card = new Card(cardID, resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3));

            getConnection().close();
            return card;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateDeck(Deck deck, String token) {
        // use CTE (common table expression)
        //TODO fix this query
        String updateStmt = "WITH UserDeck AS (" +
                "    SELECT UserID, DeckID FROM Users WHERE Token = ?" +
                ") " +
                "UPDATE Decks " +
                "SET " +
                "    Card1 = (SELECT CardID FROM Stacks WHERE UserID = UserDeck.UserID LIMIT 1 OFFSET 1), " +
                "    Card2 = (SELECT CardID FROM Stacks WHERE UserID = UserDeck.UserID LIMIT 1 OFFSET 2), " +
                "    Card3 = (SELECT CardID FROM Stacks WHERE UserID = UserDeck.UserID LIMIT 1 OFFSET 3), " +
                "    Card4 = (SELECT CardID FROM Stacks WHERE UserID = UserDeck.UserID LIMIT 1 OFFSET 4) " +
                "FROM UserDeck " +
                "WHERE Decks.DeckID = UserDeck.DeckID";

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(updateStmt);
            preparedStatement.setString(1, token);

            preparedStatement.execute();
            getConnection().close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void buyPackage(String body, User user) {

    }
}
