package example.com.app.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Package {

    private ArrayList<Card> cards;

    public Package() {
        this.cards = new ArrayList<>();
    }

    public void createCards() {
    }
}
