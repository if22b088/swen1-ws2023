package example.com.app.repositories;


import example.com.app.daos.CardDAO;

import example.com.app.models.Card;
import example.com.app.models.Deck;

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
    public ArrayList<Card> getCards(String token) { return getCardDAO().getAllCards(token); }

    //gets all the cards of a users deck
    public ArrayList<Card> getDeck(String token) {
        System.out.print("JUHUUHUHU");
        //first has to query the db for the deck (gets the cardIDs)
        ArrayList<String> cards = getCardDAO().getDeck(token);


        //creates and fills the deck by getting the card information for each individual card from the db
        ArrayList<Card> deck = new ArrayList<>();
        for (String cardID : cards) {
            System.out.print(cardID);
            deck.add(getCardDAO().getSingleCard(cardID));

        }
        return deck;
    }
    public Card getSingleCard(String cardID) {
        return getCardDAO().getSingleCard(cardID);
    }

    //updates the deck of the user with a specific token
    public void updateDeck(Deck deck, String token) { getCardDAO().updateDeck(deck,token); }

    public boolean checkIfCardsExist(String[] cardIDs, String token){ return getCardDAO().checkIfCardsExist(cardIDs, token); }

    //public void update(User user) { getUserDAO().update(user); }

    //public void login(User user) { getUserDAO().login(user); }

    // public void remove(User type) {}

}
