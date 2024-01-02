package example.com.app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class User {
    @JsonAlias({"Name"})
    private String name;
    private String username;
    private String password;

    private int coins;
    //private Stack userStack;
    private Deck userDeck;
    private int stats;
    @JsonAlias({"Bio"})
    private String bio;
    @JsonAlias({"Image"})
    private String image;


    public User() {}

    public User (String username, String bio, String image) {
        this.username = username;
        this.bio = bio;
        this.image = image;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.coins = 20;
       // this.userStack = new Stack();
        //this.userDeck = new Deck();
        this.stats = 100;
    }

    public void manageCards() {

    }

    public void purchasePackage() {
        if (this.coins >= 5) {
            //create Object
            //java.lang.Package myPackage = new java.lang.Package();
            //adds them to the users deck
           // myPackage.createCards();
            this.coins -= 5;
        } else {
            System.out.printf("Not enough coins");
        }

    }

    public void requestBattle(User opponent) {

    }

    public void acceptBattle() {

    }
/*
    public void changeStats(bool win) {
        if (win==TRUE) {
            this.stats += 3;
        } else {
            this.stats -= 5;
        }
    }

 */
}
