package example.com.app.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class Deck {

    private String card1;
    private String card2;
    private String card3;
    private String card4;


    public Deck(){}
    public Deck(String card1, String card2, String card3, String card4){
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.card4 = card4;
    };



    public boolean cardMissing() {
        return card1.isEmpty() || card2.isEmpty() || card3.isEmpty() || card4.isEmpty();
    }

}
