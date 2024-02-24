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

    private final Object lock = new Object();

    private static int waitingUsers = 0;

    private static Battle availableBattle = new Battle();

    private static int winner = 0;


    public BattleController(BattleRepository battleRepository, UserRepository userRepository, CardRepository cardRepository) {
        setBattleRepository(battleRepository);
        setUserRepository(userRepository);
        setCardRepository(cardRepository);
    }


    public Response carryOutBattle(String token) {

            User user = getUserRepository().getUserByToken(token);
            String battleLog = "";

            //if user exists
            if (user != null) {
                //check if there are existing battles that don't have a second user and the first user != this user
                /*
                Battle battleAvailable;
                synchronized (this) {
                    battleAvailable = getBattleRepository().checkForBattles(user);
                }
                */


                //if such a battle does not exist then add a battle and wait until a second user has joined the battle
                // and then return the battlelog
                synchronized (this) {
                    if (waitingUsers == 0) {
                      int newBattleID;
                      newBattleID = getBattleRepository().addBattle(user);
                      availableBattle.setBattleID(newBattleID);
                      availableBattle.setUser1(user.getUsername());
                      waitingUsers++;
                    }
                }
                if(availableBattle.getUser1().equals(user.getUsername())) {
                    //wait until user joins battle and battle is finished
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }


                    //now that the battle has finished get battlelog from the db and return it
                    battleLog = getBattleRepository().getBattleLog(availableBattle.getBattleID());
                    synchronized(this ) {
                        return new Response(
                                HttpStatus.OK,
                                ContentType.TEXT,
                                "The battle has been carried out successfully.\n" + battleLog
                        );
                    }
                    //if a battle is already available and waiting (second user is empty)

                }
                if (waitingUsers == 1){
                    synchronized(this) {
                        int winner = -1;
                        User user1 = getUserRepository().getUserByUsername(availableBattle.getUser1());
                        System.out.println(availableBattle.getUser1());
                        System.out.println(user1.getToken());
                        User user2 = user;
                        System.out.println("JUHU");
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
                            battleLog += fight(user1Card, user2Card);
                            if (winner == 1) {
                                //remove user2Card from deck2 and add it to deck of user1
                                deck2.remove(user2Card);
                                deck1.add(user2Card);
                                if (deck2.isEmpty()) {
                                    battleLog += user1.getUsername()+ " defeated " + user2.getUsername() +"!";
                                    //update userStats (wins, losses, elo)
                                    user2.setLosses(user2.getLosses()-1);
                                    user1.setWins(user1.getWins()+1);
                                    user2.setElo(user2.getElo()-5);
                                    user1.setElo(user1.getElo()+3);
                                    getUserRepository().updateUserStats(user1);
                                    getUserRepository().updateUserStats(user2);
                                    break;
                                }
                            } else if (winner == 2) {
                               // remove user1Card from deck1 and add it to deck of user2
                                deck1.remove(user1Card);
                                deck2.add(user1Card);
                                if (deck1.isEmpty()) {
                                    battleLog += user2.getUsername()+ " defeated " + user1.getUsername() +"!";
                                    //update userStats (wins, losses, elo)
                                    user1.setLosses(user1.getLosses()-1);
                                    user2.setWins(user2.getWins()+1);
                                    user1.setElo(user1.getElo()-5);
                                    user2.setElo(user2.getElo()+3);
                                    getUserRepository().updateUserStats(user1);
                                    getUserRepository().updateUserStats(user2);
                                    break;
                                }
                            }

                        }
                        //battleLog = getBattleRepository().getBattleLog(battleAvailable.getBattleID());
                        //write the battleLog in the database
                        getBattleRepository().updateBattleLog(availableBattle.getBattleID(), battleLog);
                        synchronized(lock) {
                            lock.notify(); // Notify waiting thread that a battle is ready
                            waitingUsers = 0;
                        }
                    }
                }

            //if user does not exist
            } else {
                synchronized (this) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"error\": \"Access token is missing or invalid\", \"data\": null }"
                    );
                }
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

        //both cards have monstertype
        if (cardA.getCardType().equals("monster") && cardB.getCardType().equals("monster")) {
            battleLog.append(resolveMonsterFight(cardA, cardB));
        //both cards have spell type
        } else if (cardA.getCardType().equals("spell") && cardB.getCardType().equals("spell")) {
            battleLog.append(resolveSpellFight(cardA, cardB));
        //card A is monster, card B is spell
        } else if (cardA.getCardType().equals("monster")) {
            battleLog.append(resolveMixedFight(cardA, cardB));
        //card B is monster, card A is Spell
        } else {
            battleLog.append(resolveMixedFight(cardB, cardA));
        }
        return battleLog.toString();
    }

    //logic for two monster cards
    private static String resolveMonsterFight(Card cardA, Card cardB) {

        if (cardA.getCardName().equals("Kraken") || cardB.getCardName().equals("Kraken")) {
            winner = 1;
            return "The Kraken is immune against spells.\n";
        }

        if (cardA.getCardName().equals("Dragon") && cardB.getCardName().equals("Goblin") ||
                cardA.getCardName().equals("Wizard") && cardB.getCardName().equals("Orc") ||
                cardA.getCardName().equals("Knight") && cardB.getCardName().equals("WaterSpell") ||
                cardB.getCardName().equals("Dragon") && cardA.getCardName().equals("Goblin") ||
                cardB.getCardName().equals("Wizard") && cardA.getCardName().equals("Orc") ||
                cardB.getCardName().equals("Knight") && cardA.getCardName().equals("WaterSpell")) {
            winner = 2;
            return "The " + cardA.getCardName() + " is not able to damage the " + cardB.getCardName() + ".\n";
        }

        if (cardA.getCardName().equals("FireElf") && cardB.getCardName().equals("Dragon")) {
            winner = 1;
            return "The FireElves know Dragons since they were little and can evade their attacks.\n";
        }

        if (cardA.getDamage() == cardB.getDamage()) {
            winner = 0;
            return "Draw (no action)\n";
        }

        if (cardA.getDamage() > cardB.getDamage()) {
            winner = 1;
            return "PlayerA: " + cardA.getCardName() + " defeats PlayerB: " + cardB.getCardName() + "\n";
        } else {
            winner = 2;
            return "PlayerB: " + cardB.getCardName() + " defeats PlayerA: " + cardA.getCardName() + "\n";
        }
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
            winner = 1;
            return "PlayerA: " + cardA.getCardName() + " wins\n";
        } else if (effectiveDamageA < effectiveDamageB) {
            winner = 2;
            return "PlayerB: " + cardB.getCardName() + " wins\n";
        } else {
            winner = 0;
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
                    winner = 1;
                    battleLog.append("PlayerA: ").append(monsterCard.getCardName()).append(" wins\n");
                } else if (effectiveDamage < spellCard.getDamage()) {
                    winner = 2;
                    battleLog.append("PlayerB: ").append(spellCard.getCardName()).append(" wins\n");
                } else {
                    winner = 0;
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
                    winner = 1;
                    battleLog.append("PlayerA: ").append(monsterCard.getCardName()).append(" wins\n");
                } else if (monsterCard.getDamage() < effectiveDamage) {
                    winner = 2;
                    battleLog.append("PlayerB: ").append(spellCard.getCardName()).append(" wins\n");
                } else {
                    winner = 0;
                    battleLog.append("Draw (no action)\n");
                }
            }
        }

        return battleLog.toString();
    }
}