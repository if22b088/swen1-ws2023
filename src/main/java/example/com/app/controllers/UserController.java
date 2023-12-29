package example.com.app.controllers;

import example.com.app.daos.UserDAO;
import example.com.app.models.City;
import example.com.app.models.User;
import example.com.app.repositories.UserRepository;
//import example.com.app.services.UserService;
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


    public Response updateUser(String body) {
        try {
            System.out.println(body);
            User newUser = getObjectMapper().readValue(body, User.class);
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

}
