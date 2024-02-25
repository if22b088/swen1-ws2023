
package com.example.app.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import example.com.app.daos.UserDAO;
import example.com.app.models.User;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDAOTest {

    private Connection connection;
    private UserDAO userDAO;


    @BeforeAll
    void dropAndCreateTables() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        //delete tables before running the tests
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS Cards");
            statement.executeUpdate("DROP TABLE IF EXISTS Decks");
            statement.executeUpdate("DROP TABLE IF EXISTS Users");
            statement.executeUpdate("DROP TABLE IF EXISTS Stacks");
            statement.executeUpdate("DROP TABLE IF EXISTS Packages");
            statement.executeUpdate("DROP TABLE IF EXISTS Tradings");
            statement.executeUpdate("DROP TABLE IF EXISTS Battles");
        }
        createTableAndTestData();
    }
    @BeforeEach
    void setUp() throws Exception {
        // connect to the in memmory h2 db
        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        //create the table
        userDAO = new UserDAO(connection);
    }

    @AfterEach
    void closeConnection() throws SQLException {
        connection.close();
    }


    @Test
    void testCreate() {
        User user = new User("heinz", "password");
        assertTrue(userDAO.create(user));
    }

    @Test
    void testGetAllUsers() {
        // Get all users
        ArrayList<User> users = userDAO.getAllUsers();
        assertNotNull(users);
        assertEquals(1, users.size()); // Assuming one test user is created initially
    }

    @Test
    void testGetUserByUsername() {
        // Get user by username
        User user = userDAO.getUserByUsername("testUser");
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void testGetUserByUsernameToken() {
        // Get user by username and token
        User user = userDAO.getUserByUsernameToken("testUser", "testUser-mtcgToken");
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void testGetUserByToken() {
        // Get user by token
        User user = userDAO.getUserByToken("testUser-mtcgToken");
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void testUpdateUser() {
        // Update user details
        User user = new User("testUser", "newBio", "newImage");
        assertEquals(200, userDAO.updateUser(user, "testUser-mtcgToken"));
    }

    @Test
    void testLogin() {
        // Test user login
        User user = new User("testUser", "password");
        //assertTrue(userDAO.create(user));
        assertEquals("testUser-mtcgToken", userDAO.login(user));
    }

    @Test
    //test user logout (unique mandatory feature)
    void testDeleteToken() {
        assertTrue(userDAO.deleteToken("testUser-mtcgToken"));
    }



    // createTable and testData
    void createTableAndTestData() throws SQLException {
        connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS Cards (" +
                        "CardID VARCHAR(255) PRIMARY KEY," +
                        "CardName VARCHAR(255) UNIQUE," +
                        "CardType VARCHAR(255)," +
                        "Damage INT" +
                        ")");

        connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS Decks (" +
                        "DeckID SERIAL PRIMARY KEY," +
                        "Card1 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE," +
                        "Card2 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE," +
                        "Card3 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE," +
                        "Card4 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE" +
                        ")");

        connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS Users (" +
                        "UserID SERIAL PRIMARY KEY," +
                        "Username VARCHAR(255) UNIQUE," +
                        "Password VARCHAR(255)," +
                        "Name VARCHAR(255)," +
                        "Token VARCHAR(255) UNIQUE," +
                        "Coins INT," +
                        "DeckID INT REFERENCES Decks(DeckID) UNIQUE," +
                        "Bio TEXT," +
                        "Elo INT," +
                        "Wins INT," +
                        "Losses INT," +
                        "Image VARCHAR(255)" +
                        ")");

        connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS Stacks (" +
                        "StackID SERIAL PRIMARY KEY," +
                        "UserID INT REFERENCES Users(UserID)," +
                        "CardID VARCHAR(255) REFERENCES Cards(CardID)" +
                        ")");

        connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS Packages (" +
                        "PackageID SERIAL PRIMARY KEY," +
                        "Card1 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE," +
                        "Card2 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE," +
                        "Card3 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE," +
                        "Card4 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE," +
                        "Card5 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE" +
                        ")");

        connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS Tradings (" +
                        "UsernameOfferer VARCHAR(255)," +
                        "TradingID VARCHAR(255) PRIMARY KEY," +
                        "CardToTrade VARCHAR(255)," +
                        "CardType VARCHAR(255)," +
                        "MinimumDamage INT" +
                        ")");

        connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS Battles (" +
                        "BattleID SERIAL PRIMARY KEY," +
                        "User1 VARCHAR(255) REFERENCES Users(Username) UNIQUE," +
                        "User2 VARCHAR(255) REFERENCES Users(Username)," +
                        "BattleLog TEXT" +
                        ")");

        //create testuser in db
        connection.createStatement().executeUpdate(
                "INSERT INTO Users (Username, Password, Token) VALUES ('testUser', 'password', 'testUser-mtcgToken')"
        );
    }
}