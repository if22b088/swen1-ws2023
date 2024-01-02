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


//todo add token bei update und get user
//todo add status code responses
public class UserController extends Controller{
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserRepository userRepository;


    public UserController(UserRepository userRepository) {
        setUserRepository(userRepository);
    }

    public Response getUserByUsername(String username,String token) {
        try {
            if (token != null) {
                User user = getUserRepository().getUser(username, token);
                String userDataJSON = getObjectMapper().writeValueAsString(user);
                if (user != null) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": " + userDataJSON + ", \"error\": null }"
                    );
                } else {
                    return new Response(
                            HttpStatus.NOT_FOUND,
                            ContentType.JSON,
                            "{ \"error\": \"User not found\", \"data\": null }"
                    );
                }

            } else {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
                );
            }
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
            getUserRepository().addUser(newUser);
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
    public Response updateUser(String username, String body, String token) {
        try {
            if (token != null) {
                System.out.println(body);
                User newUser = getObjectMapper().readValue(body, User.class);
                newUser.setUsername(username);

                getUserRepository().updateUser(newUser, token);

                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ \"data\": \"User successfully updated\", \"error\": null }"
                );
            }else {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
                );
            }
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
            String token = getUserRepository().loginUser(newUser);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + token + ", \"error\": null }"
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
