package example.com.app.controllers;

import example.com.app.models.User;
import example.com.app.repositories.UserRepository;
import example.com.app.repositories.BattleRepository;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import example.com.server.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class BattleController extends Controller {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserRepository userRepository;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private BattleRepository battleRepository;


    public BattleController(BattleRepository battleRepository, UserRepository userRepository) {
        setBattleRepository(battleRepository);
        setUserRepository(userRepository);
    }

    public Response carryOutBattle(String token) {
        User user = getUserRepository().getUserByToken(token);
        String battleLog = "";
        //if user exists
        if (user != null) {
            //check if there are existing battles that don't have a second user and the first user != this user
            int battleIdAvailable = getBattleRepository().checkForBattles(user);
            //if such a battle does not exist then add a battle and wait until a second user has joined the battle
            // and then return the battlelog
            if (battleIdAvailable < 0) {
                int newBattleID = getBattleRepository().addBattle(user);
                while (!getBattleRepository().checkIfBattleFinished(newBattleID)) {
                    //todo set thread to sleep/wait for 1 second
                    //sleep
                }
                battleLog = getBattleRepository().getBattleLog(newBattleID);
            } else {
                battleLog = getBattleRepository().getBattleLog(battleIdAvailable);
            }
        //if user does not exist
        } else {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
            );
        }

        return new Response(
                HttpStatus.OK,
                ContentType.TEXT,
                "The battle has been carried out successfully.\n" + battleLog
        );
    }
}