package example.com.app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {
    @JsonAlias({"Id"})
    private String cardID;
    @JsonAlias({"Name"})
    private String cardName;
    //@JsonAlias({"Type"})
    private String cardType;
    @JsonAlias({"Damage"})
    private int damage;
    private Boolean traded;
    public Card() {}

    public Card (String cardID, String cardName, int damage) {
        this.cardID = cardID;
        this.cardName = cardName;
        if (cardName.contains("Spell")) {
            this.cardType = "spell";
        } else {
            this.cardType = "monster";
        }

        this.damage = damage;
    }
}
