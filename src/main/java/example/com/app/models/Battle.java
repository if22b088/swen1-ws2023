package example.com.app.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Battle {

    private int battleID;
    private String User1;
    private String User2;
    private String[] battleLog;

    public Battle(int battleID, String User1, String User2, String battleLog) {
    }

    public Battle() {

    }
}
