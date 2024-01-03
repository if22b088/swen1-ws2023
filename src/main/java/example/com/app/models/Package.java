package example.com.app.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Package {

    private String cardID;
    private String card1;
    private String card2;
    private String card3;
    private String card4;
    private String card5;


    public Package(String cardID, String card1, String card2, String card3, String card4, String card5) {
        this.cardID = cardID;
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.card4 = card4;
        this.card5 = card5;
    }

    public void createCards() {
    }
}
