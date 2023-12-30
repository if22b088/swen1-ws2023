package example.com.app.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Card {
    private String cardName;
    private String elementType;
    private String damage;
    private Boolean inDeck;
    private Boolean traded;

    public abstract void attack();
}
