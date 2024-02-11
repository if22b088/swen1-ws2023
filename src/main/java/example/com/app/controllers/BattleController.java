package example.com.app.controllers;

import example.com.app.models.Card;
import example.com.app.models.User;

import example.com.app.models.Battle;
import example.com.app.repositories.UserRepository;
import example.com.app.repositories.CardRepository;
import example.com.app.repositories.BattleRepository;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import example.com.server.Response;
import java.util.Random;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class BattleController extends Controller {

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserRepository userRepository;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private CardRepository cardRepository;


    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private BattleRepository battleRepository;



    //"#define" ROUNDS = 100;
    private static final int MAX_ROUNDS= 100;


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
            Battle battleAvailable = getBattleRepository().checkForBattles(user);
            //if such a battle does not exist then add a battle and wait until a second user has joined the battle
            // and then return the battlelog
            if (battleAvailable.getBattleID() < 0) {
                int newBattleID = getBattleRepository().addBattle(user);
                //wait until user joins battle and battle is finished
                while (!getBattleRepository().checkIfBattleFinished(newBattleID)) {
                    //todo set thread to sleep/wait for 1 second
                    //sleep
                }
                //now that the battle has finished get battlelog from the db and return it
                battleLog = getBattleRepository().getBattleLog(newBattleID);
                return new Response(
                        HttpStatus.OK,
                        ContentType.TEXT,
                        "The battle has been carried out successfully.\n" + battleLog
                );
            } else {
                //todo implement battleLogic here
                User user1 = new User();
                user1.setUsername(battleAvailable.getUser1());
                User user2 = user;
                ArrayList<Card> deck1 = getCardRepository().getDeck(user1.getToken());
                ArrayList<Card> deck2 = getCardRepository().getDeck(user2.getToken());

                for (int rounds = 1; rounds <= MAX_ROUNDS; rounds++) {
                    //get random card from user1 deck
                    Random random = new Random();
                    int randomIndex = random.nextInt(deck1.size());
                    // Get the card at the random index
                    Card user1Card = deck1.get(randomIndex);

                    //get random card from user2 deck
                    random = new Random();
                    randomIndex = random.nextInt(deck2.size());
                    // Get the card at the random index
                    Card user2Card = deck2.get(randomIndex);
                    //let the cards fight
                    battleLog += fight(user1Card,user2Card);

                }
                //battleLog = getBattleRepository().getBattleLog(battleAvailable.getBattleID());
                //write the battleLog in the database
                getBattleRepository().updateBattleLog(battleAvailable.getBattleID(),battleLog);
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

    //fight
    //decides what kind of card types are battling
    public static String fight(Card cardA, Card cardB) {
        //used stringbuilder because it is more efficient than normal string
        StringBuilder battleLog = new StringBuilder();

        if (cardA.getCardType().equals("monster") && cardB.getCardType().equals("monster")) {
            battleLog.append(resolveMonsterFight(cardA, cardB));
        } else if (cardA.getCardType().equals("spell") && cardB.getCardType().equals("spell")) {
            battleLog.append(resolveSpellFight(cardA, cardB));
        } else if (cardA.getCardType().equals("monster")) {
            battleLog.append(resolveSpellFight(cardA, cardB));
        } else {
            battleLog.append(resolveSpellFight(cardB, cardA));
        }
        return battleLog.toString();
    }

    //logic for two monster cards
    private static String resolveMonsterFight(Card cardA, Card cardB) {

        if (cardA.getCardName().equals("Kraken") || cardB.getCardName().equals("Kraken")) {
            return "The Kraken is immune against spells.\n";
        }

        if (cardA.getCardName().equals("Dragon") && cardB.getCardName().equals("Goblin") ||
                cardA.getCardName().equals("Wizard") && cardB.getCardName().equals("Orc") ||
                cardA.getCardName().equals("Knight") && cardB.getCardName().equals("WaterSpell") ||
                cardB.getCardName().equals("Dragon") && cardA.getCardName().equals("Goblin") ||
                cardB.getCardName().equals("Wizard") && cardA.getCardName().equals("Orc") ||
                cardB.getCardName().equals("Knight") && cardA.getCardName().equals("WaterSpell")) {
            return "The " + cardA.getCardName() + " is not able to damage the " + cardB.getCardName() + ".\n";
        }

        if (cardA.getCardName().equals("FireElf") && cardB.getCardName().equals("Dragon")) {
            return "The FireElves know Dragons since they were little and can evade their attacks.\n";
        }

        if (cardA.getDamage() == cardB.getDamage()) {
            return "Draw (no action)\n";
        }

        return cardA.getDamage() > cardB.getDamage() ?
                "PlayerA: " + cardA.getCardName() + " defeats PlayerB: " + cardB.getCardName() + "\n" :
                "PlayerB: " + cardB.getCardName() + " defeats PlayerA: " + cardA.getCardName() + "\n";
    }

    //logic for two spell cards
    private static String resolveSpellFight(Card cardA, Card cardB) {
        int effectiveDamageA = cardA.getDamage();
        int effectiveDamageB = cardB.getDamage();

        if (cardA.getCardName().equals("WaterSpell") && cardB.getCardName().equals("FireSpell") ||
                cardA.getCardName().equals("NormalSpell") && cardB.getCardName().equals("WaterSpell") ||
                cardB.getCardName().equals("FireSpell") && cardA.getCardName().equals("NormalSpell")) {
            effectiveDamageA *= 2;
        } else if (cardA.getCardName().equals("FireSpell") && cardB.getCardName().equals("NormalSpell") ||
                cardB.getCardName().equals("WaterSpell") && cardA.getCardName().equals("FireSpell") ||
                cardB.getCardName().equals("NormalSpell") && cardA.getCardName().equals("WaterSpell")) {
            effectiveDamageA /= 2;
        }

        if (cardB.getCardName().equals("WaterSpell") && cardA.getCardName().equals("FireSpell") ||
                cardB.getCardName().equals("NormalSpell") && cardA.getCardName().equals("WaterSpell") ||
                cardA.getCardName().equals("FireSpell") && cardB.getCardName().equals("NormalSpell")) {
            effectiveDamageB *= 2;
        } else if (cardB.getCardName().equals("FireSpell") && cardA.getCardName().equals("NormalSpell") ||
                cardA.getCardName().equals("WaterSpell") && cardB.getCardName().equals("FireSpell") ||
                cardA.getCardName().equals("NormalSpell") && cardB.getCardName().equals("WaterSpell")) {
            effectiveDamageB /= 2;
        }

        if (effectiveDamageA > effectiveDamageB) {
            return "PlayerA: " + cardA.getCardName() + " wins\n";
        } else if (effectiveDamageA < effectiveDamageB) {
            return "PlayerB: " + cardB.getCardName() + " wins\n";
        } else {
            return "Draw (no action)\n";
        }
    }

    //logic for a spell and a monster card
    private static String resolveMixedFight(Card monsterCard, Card spellCard) {
        StringBuilder battleLog = new StringBuilder();

        if (monsterCard.getCardType().equals("monster")) {
            if (spellCard.getCardName().equals("WaterSpell") && monsterCard.getCardName().equals("Knight")) {
                battleLog.append("The armor of Knights is so heavy that WaterSpells make them drown them instantly.\n");
            } else if (monsterCard.getCardName().equals("Kraken") || spellCard.getCardName().equals("Kraken")) {
                battleLog.append("The Kraken is immune against spells.\n");
            } else if (spellCard.getCardName().equals("Dragon") && monsterCard.getCardName().equals("Goblin")) {
                battleLog.append("Goblins are too afraid of Dragons to attack.\n");
            } else if (spellCard.getCardName().equals("Wizard") && monsterCard.getCardName().equals("Orc")) {
                battleLog.append("Wizard can control Orks so they are not able to damage them.\n");
            } else if (spellCard.getCardName().equals("FireElf") && monsterCard.getCardName().equals("Dragon")) {
                battleLog.append("The FireElves know Dragons since they were little and can evade their attacks.\n");
            } else {
                int effectiveDamage = monsterCard.getDamage();
                if (spellCard.getCardType().equals("spell")) {
                    if (spellCard.getCardName().equals("WaterSpell")) {
                        effectiveDamage *= 2;
                    } else if (spellCard.getCardName().equals("FireSpell")) {
                        effectiveDamage /= 2;
                    }
                }

                if (effectiveDamage > spellCard.getDamage()) {
                    battleLog.append("PlayerA: ").append(monsterCard.getCardName()).append(" wins\n");
                } else if (effectiveDamage < spellCard.getDamage()) {
                    battleLog.append("PlayerB: ").append(spellCard.getCardName()).append(" wins\n");
                } else {
                    battleLog.append("Draw (no action)\n");
                }
            }
        } else if (spellCard.getCardType().equals("monster")) {
            int effectiveDamage = spellCard.getDamage();
            if (monsterCard.getCardName().equals("WaterSpell") && spellCard.getCardName().equals("Knight")) {
                battleLog.append("The armor of Knights is so heavy that WaterSpells make them drown them instantly.\n");
            } else if (monsterCard.getCardName().equals("Kraken") || spellCard.getCardName().equals("Kraken")) {
                battleLog.append("The Kraken is immune against spells.\n");
            } else if (monsterCard.getCardName().equals("Dragon") && spellCard.getCardName().equals("Goblin")) {
                battleLog.append("Goblins are too afraid of Dragons to attack.\n");
            } else if (monsterCard.getCardName().equals("Wizard") && spellCard.getCardName().equals("Orc")) {
                battleLog.append("Wizard can control Orks so they are not able to damage them.\n");
            } else if (monsterCard.getCardName().equals("FireElf") && spellCard.getCardName().equals("Dragon")) {
                battleLog.append("The FireElves know Dragons since they were little and can evade their attacks.\n");
            } else {
                if (monsterCard.getCardType().equals("spell")) {
                    if (monsterCard.getCardName().equals("WaterSpell")) {
                        effectiveDamage *= 2;
                    } else if (monsterCard.getCardName().equals("FireSpell")) {
                        effectiveDamage /= 2;
                    }
                }

                if (monsterCard.getDamage() > effectiveDamage) {
                    battleLog.append("PlayerA: ").append(monsterCard.getCardName()).append(" wins\n");
                } else if (monsterCard.getDamage() < effectiveDamage) {
                    battleLog.append("PlayerB: ").append(spellCard.getCardName()).append(" wins\n");
                } else {
                    battleLog.append("Draw (no action)\n");
                }
            }
        }

        return battleLog.toString();
    }
}