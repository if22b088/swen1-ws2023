package example.com.app.controllers;
import com.fasterxml.jackson.core.JsonProcessingException;

import example.com.app.models.Card;
import example.com.app.models.User;
import example.com.app.repositories.CardRepository;
import example.com.app.repositories.UserRepository;
import example.com.app.repositories.PackageRepository;


import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import example.com.server.Response;


import java.util.ArrayList;
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

    public PackageController(CardRepository cardRepository, UserRepository userRepository, PackageRepository packageRepository) {
        setCardRepository(cardRepository);
        setUserRepository(userRepository);
        setPackageRepository((packageRepository));

    }

    //todo: finish createPackage
    public Response createPackage(String body, String token) {
        try {
            //check if user is admin
            User user = getUserRepository().getUser("admin", token);
            //check if user exists/token is valid by check if username is set
            if (user.getUsername().isEmpty()) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"error\": Access token is missing or invalid, \"data\": null }"
                );
            }

            //check if user is admin
            if (!user.getUsername().equals("admin")) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"error\": \"Provided user is not \"admin\", \"data\": null }"
                );
            }

            //fills the cardlist pack with cards from the body and checks if each of the cards already exists in the db
            ArrayList<Card> pack = getObjectMapper().readValue(body, getObjectMapper().getTypeFactory().constructCollectionType(List.class, Card.class));
            for (Card card : pack) {
                //checks whether the card exists by trying to retrieve the card from the db
                Card cardTemp = getCardRepository().getSingleCard(card.getCardID());
                //if the card does not yet exist add it to the pack
                if (cardTemp != null)  {
                    return new Response(
                            HttpStatus.CONFLICT,
                            ContentType.JSON,
                            "{ \"error\": \"At least one card in the packages already exists\", \"data\": null }"
                    );
                }
            }

            //create the package
            int statusCode = getPackageRepository().createPackage(pack);
            if (statusCode == 201) {
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ \"data\": " + body + "Package and cards successfully created" + ", \"error\": null }"
                );
            } else {
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"error\": \"Internal Server Error\", \"data\": null }"
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

    //todo: finish buyPackage: done?
    public Response buyPackage(String body, String token) {

        try {
            User user = getUserRepository().getUserByToken(token);
            boolean packExists = getPackageRepository().checkIfPackageAvailable();
            if (!packExists) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": \"No card package available for buying\", \"data\": null }"
                );
            }
            //if user exists and token matches user
            if (user != null) {
                //user has more than 5 coins -> buy package
                if (user.getCoins() >= 5) {
                    String[] cardIDs = getPackageRepository().buyPackage(user);

                    if (cardIDs == null) {
                        return new Response(
                                HttpStatus.NOT_FOUND,
                                ContentType.JSON,
                                "{ \"error\": \"No card package available for buying\", \"data\": null }"
                        );

                    }
                    ArrayList<Card> cardsList = new ArrayList<>();
                    for (String cardID : cardIDs) {
                        Card card = getCardRepository().getSingleCard(cardID);
                        cardsList.add(card);
                    }
                    String packageDataJSON = getObjectMapper().writeValueAsString(cardsList);

                    if (cardsList != null) {
                        return new Response(
                                HttpStatus.OK,
                                ContentType.JSON,
                                "{ \"data\": "+ packageDataJSON +", \"data\": null }"
                        );
                    } else {

                    }
                //not enough coins
                } else {
                    return new Response(
                            HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"error\": \"Not enough money for buying a card package\", \"data\": null }"
                    );
                }
            //user does not exist or token does not match
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
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"error\": \"Internal Server Error\", \"data\": null }"
        );
    }
}
