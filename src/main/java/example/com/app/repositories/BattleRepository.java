package example.com.app.repositories;
import example.com.app.daos.BattleDAO;
import example.com.app.models.User;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class BattleRepository {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    BattleDAO battleDAO;

    public BattleRepository(BattleDAO battleDAO) {setBattleDAO(battleDAO);}

    public int checkForBattles(User user) {return getBattleDAO().getPendingBattles(user);}

    public int addBattle(User user) {return getBattleDAO().createBattle(user);}
    public Boolean checkIfBattleFinished(int battleID)  { return getBattleDAO().checkIfBattleComplete(battleID);}

    public String getBattleLog(int battleID) {return getBattleDAO().getBattleLog(battleID);}

}
