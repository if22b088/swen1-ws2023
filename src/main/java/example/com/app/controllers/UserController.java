package example.com.app.controllers;

import example.com.app.daos.UserDAO;
import example.com.app.models.User;
import example.com.app.repositories.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import example.com.server.Response;

import java.util.List;

public class UserController extends Controller{
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserRepository userRepository;


    public UserController(UserRepository userRepository) {
        setUserRepository(userRepository);
    }

    public Response getUserByUsername(String username) {
        try {
            User user = getUserRepository().get(username);
            String userDataJSON = getObjectMapper().writeValueAsString(user);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + userDataJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

    //creates user if does not exist
    public Response createUser(String body) {
        try {
            System.out.println(body);
            User newUser = getObjectMapper().readValue(body, User.class);
            getUserRepository().add(newUser);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + body + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }


    //updates name, bio, image for specific user
    public Response updateUser(String username, String body) {
        try {
            System.out.println(body);
            User newUser = getObjectMapper().readValue(body, User.class);
            newUser.setUsername(username);
            getUserRepository().update(newUser);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + body + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }


    //logs in with specific user
    public Response loginUser(String body){
        try {
            System.out.println(body);
            User newUser = getObjectMapper().readValue(body, User.class);
            getUserRepository().login(newUser);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + body + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

}
