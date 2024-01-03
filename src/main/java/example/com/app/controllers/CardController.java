package example.com.app.controllers;

import example.com.app.daos.UserDAO;
import example.com.app.models.Card;
import example.com.app.models.Deck;
import example.com.app.models.User;
import example.com.app.repositories.CardRepository;
import example.com.app.repositories.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import example.com.server.Response;

import java.util.List;

public class CardController extends Controller {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private CardRepository cardRepository;


    public CardController(CardRepository cardRepository) {
        setCardRepository(cardRepository);
    }

//todo check for admin
    //gets all cards from user (from that users stack) who is associated with token
    public Response getCards(String token) {
        try {
            //if a token is set/exists
            if (token != null) {
                List<Card> cardData = getCardRepository().getAll(token);
                String cardDataJSON = getObjectMapper().writeValueAsString(cardData);
                //if user has cards
                if (cardData != null) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": " + cardDataJSON + ", \"error\": null }"
                    );
                // user does not have any cards
                } else {
                    return new Response(
                            HttpStatus.NO_CONTENT,
                            ContentType.JSON,
                            "{ \"error\": \"The request was fine, but the user doesn't have any cards\", \"data\": null }"
                    );
                }
            //if no token was sent
            }else {
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

    //gets Deck (four cards) from user who is associated with token
    public Response getDeck(String token) {
        try {
            //if a token is set/exists
            if (token != null) {
                List<Card> deckData = getCardRepository().getDeck(token);
                String deckDataJSON = getObjectMapper().writeValueAsString(deckData);
                if (deckData != null) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": " + deckDataJSON + ", \"error\": null }"
                    );
                } else {
                    return new Response(
                            HttpStatus.NO_CONTENT,
                            ContentType.JSON,
                            "{ \"error\": \"The request was fine, but the deck doesn't have any cards\", \"data\": null }"
                    );
                }
            //if no token was sent
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

    //upates the 4 cards of a users deck
    public Response updateDeck(String body, String token) {
        //todo: write conditions for different responses (for 403)
        try {
            if (token != null) {
                System.out.println(body);
                Deck newDeck = getObjectMapper().readValue(body, Deck.class);

                //checks if new deck contains < 4 cards
                if (newDeck.cardMissing()) {
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.JSON,
                            "{ \"error\": \"The provided deck did not include the required amount of cards\", \"data\": null }"
                    );
                } else {
                    getCardRepository().updateDeck(newDeck, token);
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": " + body + "The deck has been successfully configured" + ", \"error\": null }"
                    );
                }
            //if no token was sent
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


    //todo: finish createPakage
    public Response createPackage(String body, String token) {
        try {
            List<Card> pack = null;
            for (int i = 0; i < 5; i++) {
                getObjectMapper().readValue(body, Card.class);
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
