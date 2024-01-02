package example.com.app.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {
    private String cardID;
    private String cardName;
    private String cardType;
    private int damage;
    private Boolean traded;
    public Card() {}

    public Card (String cardID, String cardName, String cardType, int damage) {
        this.cardID = cardID;
        this.cardName = cardName;
        this.cardType =cardType;
        this.damage = damage;
    }
    //public abstract void attack();
}
