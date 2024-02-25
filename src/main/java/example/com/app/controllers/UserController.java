package example.com.app.controllers;
import com.fasterxml.jackson.annotation.JsonInclude;
import example.com.app.daos.UserDAO;
import example.com.app.models.Card;
import example.com.app.models.User;
import example.com.app.repositories.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import example.com.server.Response;

import java.util.ArrayList;
import java.util.List;

public class UserController extends Controller{
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserRepository userRepository;


    public UserController(UserRepository userRepository) {
        setUserRepository(userRepository);
    }

    public Response getUserByUsernameToken(String username,String token) {
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
            User newUser = getObjectMapper().readValue(body, User.class);
            boolean created = getUserRepository().addUser(newUser);
            if (created) {
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ \"data\": " + body + ", \"error\": null }"
                );
            } else {
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "{ \"error\": \"User with same username already registered\", \"data\": null }"
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

    //updates name, bio, image for specific user
    public Response updateUser(String username, String body, String token) {
        try {
            if (token != null) {
                User newUser = getObjectMapper().readValue(body, User.class);
                System.out.println("NEW USER: username: "+newUser.getUsername()+ "token:"+ newUser.getToken());
                newUser.setUsername(username);

                int statusCode = getUserRepository().updateUser(newUser, token);

                if (statusCode == 200) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": \"User successfully updated\", \"error\": null }"
                    );
                } else if (statusCode == 401) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
                    );
                } else if (statusCode == 404) {
                    return new Response(
                            HttpStatus.NOT_FOUND,
                            ContentType.JSON,
                            "{ \"error\": \"User not found\", \"data\": null }"
                    );
                } else {
                    return new Response(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ContentType.JSON,
                            "{ \"error\": \"Internal Server Error\", \"data\": null }"
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


    //logs in with specific user
    public Response loginUser(String body){
        try {
            User newUser = getObjectMapper().readValue(body, User.class);
            String token = getUserRepository().loginUser(newUser);
            if (token != null) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"data\": " + token + ", \"error\": null }"
                );
            } else {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"error\": \"Invalid username/password provided\", \"data\": null }"
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

    public Response getStats(String token) {
        try {
            if (token != null) {
                User user = getUserRepository().getUserByToken(token);
                User tmpUser = new User(user.getName(),user.getElo(),user.getWins(),user.getLosses());

                // Configure ObjectMapper to exclude null values
                getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

                String userDataJSON = getObjectMapper().writeValueAsString(tmpUser);
                if (!tmpUser.getName().isEmpty()) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": " + userDataJSON + ", \"error\": null }"
                    );
                } else {
                    return new Response(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ContentType.JSON,
                            "{ \"error\": \"Internal Server Error\", \"data\": null }"
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


    public Response getScoreboard(String token) {

        try {
            if (token != null) {
                ArrayList<User> users = getUserRepository().getAllUsers();

                String userDataJSON = getObjectMapper().writeValueAsString(users);
                if (!users.isEmpty()) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": " + userDataJSON + ", \"error\": null }"
                    );
                } else {
                    return new Response(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ContentType.JSON,
                            "{ \"error\": \"Internal Server Error\", \"data\": null }"
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


    //logout for user by deleting the token in the db
    public Response deleteToken(String token) {
        if (token != null) {
            boolean deleted = getUserRepository().deleteToken(token);
            if (deleted) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"data\": \"User successfully logged out.\", \"error\": null }"
                );
            } else {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": \"No current active session for given token\", \"data\": null }"
                );
            }
        } else {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"error\": \"Invalid token provided\", \"data\": null }"
            );
        }
    }
}
