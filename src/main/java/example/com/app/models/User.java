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

    private int userID;
    @JsonAlias({"Name"})
    private String name;
    @JsonAlias({"Username"})
    private String username;
    @JsonAlias({"Password"})
    private String password;

    private int coins;
    //private Stack userStack;
    private Deck userDeck;
    private String token;
    private int stats;
    @JsonAlias({"Bio"})
    private String bio;
    @JsonAlias({"Image"})
    private String image;
    private int elo;
    private int wins;
    private int losses;

    public User() {}

    public User ( String name, String bio, String image) {
        this.username = name;
        this.bio = bio;
        this.image = image;
    }

    //full user
    public User (int userID, String username, String name, String token, String bio, String image, int coins, int elo, int wins, int losses) {
        this.userID = userID;
        this.username = username;
        this.name = name;
        this.token = token;
        this.bio = bio;
        this.image = image;
        this.coins = coins;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
    }

    //constructor for /stats
    public User (String name, int elo, int wins, int losses) {
        this.name = name;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
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
