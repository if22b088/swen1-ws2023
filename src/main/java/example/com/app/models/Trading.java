package example.com.app.models;


import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

@Getter
@Setter
public class Trading {
    private String usernameOfferer;
    @JsonAlias({"Id"})
    private String id;
    @JsonAlias({"CardToTrade"})
    private String cardToTrade;
    @JsonAlias({"Type"})
    private String cardType;
    @JsonAlias({"MinimumDamage"})
    private int minDamage;


    //jackson requires default constructor
    public Trading() {

    }


    public Trading(String usernameOfferer, String id, String cardToTrade, String cardType, int minDamage ){
        this.usernameOfferer = usernameOfferer;
        this.id = id;
        this.cardToTrade =cardToTrade;
        this.cardType = cardType;
        this.minDamage = minDamage;
    }

}
