package example.com.app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.ArrayList;
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
                List<Card> cardData = getCardRepository().getCards(token);
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


    //not in use (cardRepository.getSingleCard(cardID) is used in packageRepostiory  (buyPackage))
    public Card getSingleCard(String cardID){
        return cardRepository.getSingleCard(cardID);
    }

    //gets Deck (four cards) from user who is associated with token
    public Response getDeck(String token, String format) {
        try {
            //if a token is set/exists
            if (token != null) {
                List<Card> deckData = getCardRepository().getDeck(token);

                if (!deckData.isEmpty() && deckData.get(1) != null) {
                    //check if path is /deck or /deck?format=plain

                    if (format.isEmpty()) {
                        System.out.println("STILL WORKS: format is " + format);
                        String deckDataJSON = getObjectMapper().writeValueAsString(deckData);
                        return new Response(
                                HttpStatus.OK,
                                ContentType.JSON,
                                "{ \"data\": " + deckDataJSON + ", \"error\": null }"
                        );
                    } else {
                        String plainData = "";
                        for(Card card : deckData) {
                            plainData += card.getCardID()+ " " + card.getCardName()+ " " + card.getDamage()+" ";
                        }
                        return new Response(
                                HttpStatus.OK,
                                ContentType.TEXT,
                                "The Deck has Cards the response contain these: \n"+plainData
                        );

                    }
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
        /*
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"error\": \"Internal Server Error\", \"data\": null }"
        );
        */

    }

    //upates the 4 cards of a users deck
    public Response updateDeck(String body, String token) {
        try {
            //check if token was sent
            if (token != null) {
                String[] cardIDs = getObjectMapper().readValue(body, String[].class);
                //checks if new deck (cards sent) contains < 4 cards
                if (cardIDs.length < 4) {
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.JSON,
                            "{ \"error\": \"The provided deck did not include the required amount of cards\", \"data\": null }"
                    );
                }

                boolean exists = getCardRepository().checkIfCardsExist(cardIDs, token);

                if (exists) {
                    Deck newDeck = new Deck(cardIDs[0], cardIDs[1], cardIDs[2], cardIDs[3]);

                    //call updateDeck repository method to update the deck
                    getCardRepository().updateDeck(newDeck, token);
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": " + body + "The deck has been successfully configured" + ", \"error\": null }"
                    );
                } else {
                    return new Response(
                            HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"error\": \"At least one of the provided cards does not belong to the user or is not available.\", \"data\": null }"
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
}