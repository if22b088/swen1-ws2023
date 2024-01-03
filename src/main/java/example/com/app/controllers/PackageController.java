package example.com.app.controllers;

import example.com.app.daos.UserDAO;
import example.com.app.models.Card;
import example.com.app.models.Deck;
import example.com.app.models.User;
import example.com.app.repositories.CardRepository;
import example.com.app.repositories.UserRepository;
import example.com.app.repositories.PackageRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import example.com.server.Response;

import java.util.List;

public class PackageController extends Controller {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private CardRepository cardRepository;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserRepository UserRepository;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private PackageRepository packageRepository;

    public PackageController(CardRepository cardRepository, UserRepository userRepository) {
        setCardRepository(cardRepository);
        setUserRepository(userRepository);
    }



    //todo: finish buyPackage
    public Response buyPackage(String body, String token) {

        try {
            User user = getUserRepository().getUser(body,token);

            if (user != null) {
                if (user.getCoins() >= 5) {
                    int statuscode= getPackageRepository().buyPackage(body, user);
                    if (success) {

                    }
                } else {
                    return new Response(
                            HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"error\": \"Not enough money for buying a card package\", \"data\": null }"
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
        return null;
    }
}
