package example.com.app.repositories;
import example.com.app.daos.BattleDAO;
import example.com.app.models.User;
import example.com.app.models.Battle;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class BattleRepository {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    BattleDAO battleDAO;

    public BattleRepository(BattleDAO battleDAO) {setBattleDAO(battleDAO);}

    //checks if a battle is missing user2 and user1!= user
    public Battle checkForBattles(User user) {return getBattleDAO().getPendingBattles(user);}

    //creates a new battle in the db and returns the battleiD
    public int addBattle(User user) {return getBattleDAO().createBattle(user);}

    //checks if the battle with battleID user2 and battleLOG != null
    public Boolean checkIfBattleFinished(int battleID)  { return getBattleDAO().checkIfBattleComplete(battleID);}

    //returns the battleLog of battle with battleID
    public String getBattleLog(int battleID) {return getBattleDAO().getBattleLog(battleID);}

    //updates the battleLog of a battle
    public void updateBattleLog(int battleID, String battleLog) {getBattleDAO().updateBattleLog(battleID, battleLog);}



}
