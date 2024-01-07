package example.com.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import example.com.app.models.Card;
import example.com.app.models.User;
import example.com.app.models.Trading;

import example.com.app.repositories.CardRepository;
import example.com.app.repositories.UserRepository;
import example.com.app.repositories.TradingRepository;

import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import example.com.server.Response;

import java.util.ArrayList;
import java.util.List;


public class TradingController extends Controller{
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserRepository userRepository;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private TradingRepository tradingRepository;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private CardRepository cardRepository;


    public TradingController(TradingRepository tradingRepository, UserRepository userRepository, CardRepository cardRepository) {
        setUserRepository(userRepository);
        setTradingRepository(tradingRepository);
        setCardRepository(cardRepository);
    }

    //gets all trading deals
    public Response getTradings(String token) {
        try {
            User user = getUserRepository().getUserByToken(token);
            //if a token is set/exists and the token is associated with a user
            if (token != null && user != null) {
                ArrayList<Trading> tradings = getTradingRepository().getTradings();
                //if user has cards
                if (tradings != null) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": " + tradings + ", \"error\": null }"
                    );
                    // user does not have any cards
                } else {
                    return new Response(
                            HttpStatus.NO_CONTENT,
                            ContentType.JSON,
                            "{ \"error\": \"The request was fine, but there are no trading deals available\", \"data\": null }"
                    );
                }
                //if no token was sent or token is invaild
            } else {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response createTrading(String body, String token) {
        try {
            User user = getUserRepository().getUserByToken(token);
            //if a token is set/exists and the token is associated with a user
            if (token != null && user != null) {
                //fills a trading object with body content
                Trading newTrading = getObjectMapper().readValue(body, Trading.class);
                //if body contains trading deal
                if (newTrading !=  null) {
                    int statusCode = getTradingRepository().createTrading(newTrading, user);
                    if (statusCode == 201) {
                        return new Response(
                                HttpStatus.CREATED,
                                ContentType.JSON,
                                "{ \"data\": Trading deal successfully created, \"error\": null }"
                        );
                    }else if (statusCode == 403) {
                        return new Response(
                                HttpStatus.FORBIDDEN,
                                ContentType.JSON,
                                "{ \"error\": \"The deal contains a card that is not owned by the user or locked in the deck.\", \"data\": null }"
                        );

                    } else if (statusCode == 409) {
                        return new Response(
                                HttpStatus.CONFLICT,
                                ContentType.JSON,
                                "{ \"error\": \"A deal with this ID already exists.\", \"data\": null }"
                        );
                    }
                // user does not have any cards
                } else {
                    return new Response(
                            HttpStatus.NO_CONTENT,
                            ContentType.JSON,
                            "{ \"error\": \"The request was fine, but there are no trading deals available\", \"data\": null }"
                    );
                }
            //if no token was sent or token is invaild
            } else {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
                );
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response deleteTrading(String tradeDealID, String token) {
        try {
            User user = getUserRepository().getUserByToken(token);
            //if a token is set/exists and the token is associated with a user
            if (token != null && user != null) {
                int statusCode = getTradingRepository().deleteTrading(tradeDealID, token);
                //if deletion was successful
                if (statusCode == 200) {
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"data\": \"Trading deal successfully deleted.\", \"error\": null }"
                    );
                //if card is not owned by user
                } else if (statusCode == 403 ){
                    return new Response(
                            HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"error\": \"The deal contains a card that is not owned by the user.\", \"data\": null }"
                    );
                //if deal id does not exist
                } else if (statusCode == 404 ) {
                    return new Response(
                            HttpStatus.NOT_FOUND,
                            ContentType.JSON,
                            "{ \"error\": \"The provided deal ID was not found.\", \"data\": null }"
                    );
                }

            //if no token was sent or token is invaild
            } else {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Response carryOutTrading(String body, String tradeDealID, String token) {
        try {
            Card cardToTrade = cardRepository.getSingleCard(getObjectMapper().readValue(body, String.class));
            try {
                User user = getUserRepository().getUserByToken(token);
                //if a token is set/exists and the token is associated with a user
                if (token != null && user != null) {
                    int statusCode = getTradingRepository().carryOutTrading(cardToTrade, tradeDealID, user);
                    //if trade was successful
                    if (statusCode == 200) {
                        return new Response(
                                HttpStatus.OK,
                                ContentType.JSON,
                                "{ \"data\": \"Trading deal successfully deleted.\", \"error\": null }"
                        );
                        //if card is not owned by user
                    } else if (statusCode == 403 ){
                        return new Response(
                                HttpStatus.FORBIDDEN,
                                ContentType.JSON,
                                "{ \"error\": \"The offered card is not owned by the user, or the requirements are not met (Type, MinimumDamage), or the offered card is locked in the deck.\", \"data\": null }"
                        );
                        //if deal id does not exist
                    } else if (statusCode == 404 ) {
                        return new Response(
                                HttpStatus.NOT_FOUND,
                                ContentType.JSON,
                                "{ \"error\": \"The provided deal ID was not found.\", \"data\": null }"
                        );
                    }

                    //if no token was sent or token is invaild
                } else {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
                    );
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}