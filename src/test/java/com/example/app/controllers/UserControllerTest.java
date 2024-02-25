package com.example.app.controllers;
import example.com.app.controllers.UserController;
import example.com.app.daos.UserDAO;
import example.com.app.models.Card;
import example.com.app.models.User;

import example.com.app.models.Battle;
import example.com.app.repositories.UserRepository;
import example.com.app.repositories.CardRepository;
import example.com.app.repositories.BattleRepository;
import example.com.app.services.DatabaseService;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import example.com.server.Response;
import java.util.Random;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;


public class UserControllerTest {
    @Mock
    DatabaseService databaseService = new DatabaseService();
    @Mock
    UserDAO userDAO = new UserDAO(databaseService.getConnection());

    @Mock
    UserRepository userRepository = new UserRepository(userDAO);
    @Mock
    private UserController userController = new UserController(userRepository);



    @Test
    void testUserByUserNameToken_emptyToken() {
        Response response = null;
        Response assertResponse =  new Response(HttpStatus.UNAUTHORIZED,ContentType.JSON,"{ \"error\": \"Access token is missing or invalid\", \"data\": null }");
        response = userController.getUserByUsernameToken("userNotExistent", null);

        assertEquals(assertResponse.getStatusCode(),response.getStatusCode());
        assertEquals(assertResponse.getContentType(),response.getContentType());
        assertEquals(assertResponse.getContent(),response.getContent());

    }



}
