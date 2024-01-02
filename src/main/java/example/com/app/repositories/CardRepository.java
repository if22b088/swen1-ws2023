package example.com.app.repositories;


import example.com.app.daos.CardDAO;

import example.com.app.models.Card;
import example.com.app.models.Deck;

import example.com.app.models.City;
import example.com.app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class CardRepository {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    CardDAO cardDAO;

    public CardRepository(CardDAO cardDAO) { setCardDAO(cardDAO); }

    //gets all the cards from a user (the stack)
    public ArrayList<Card> getAll(String token) { return getCardDAO().getAllCards(token); }

    //gets all the cards of a users deck
    public ArrayList<Card> getDeck(String token) {
        //first has to query the db for the deck (gets the cardIDs)
        ArrayList<String> cards = getCardDAO().getDeck(token);

        //creates and fills the deck by getting the card information for each individual card from the db
        ArrayList<Card> deck = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            deck.add(getCardDAO().getSingleCard(cards.get(i)));
        }
        return deck;
    }

    //updates the deck of the user with a specific token
    public void updateDeck(Deck deck, String token) {

        /*
        //first gets the card information of each card in the deck)
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(getCardDAO().getSingleCard(deck.getCard1()));
        cards.add(getCardDAO().getSingleCard(deck.getCard2()));
        cards.add(getCardDAO().getSingleCard(deck.getCard3()));
        cards.add(getCardDAO().getSingleCard(deck.getCard4()));
*/
        getCardDAO().updateDeck(deck,token);

    }



    //public void update(User user) { getUserDAO().update(user); }

    //public void login(User user) { getUserDAO().login(user); }

    // public void remove(User type) {}

}
