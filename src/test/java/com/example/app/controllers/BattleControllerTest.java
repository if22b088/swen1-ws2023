package com.example.app.controllers;

import example.com.app.controllers.BattleController;
import example.com.app.models.Card;
import example.com.app.models.User;
import example.com.app.repositories.BattleRepository;
import example.com.app.repositories.CardRepository;
import example.com.app.repositories.UserRepository;
import example.com.server.Response;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BattleControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private BattleRepository battleRepository;
    BattleController battleController = new BattleController(battleRepository,userRepository,cardRepository);



    @Test
    void carryOutBattle_missingToken() {
        //arrange
        Response response = null;
        Response assertResponse =  new Response(HttpStatus.UNAUTHORIZED,ContentType.JSON,"{ \"error\": \"Access token is missing or invalid\", \"data\": null }");
        //act
        response = battleController.carryOutBattle(null);
        //assert
        assertEquals(assertResponse.getStatusCode(),response.getStatusCode());
        assertEquals(assertResponse.getContentType(),response.getContentType());
        assertEquals(assertResponse.getContent(),response.getContent());
    }

    @Test
    void testFight_playerBwins() {
        Card cardDragon = new Card("1ab", "Dragon", 10);
        Card cardWaterGoblin = new Card("2ab", "WaterGoblin", 10);
        String result = battleController.fight(cardDragon, cardWaterGoblin);
        assertEquals("The Dragon is not able to damage the WaterGoblin.\n", result);
        //verify if player A has won
        assertEquals(2,battleController.getWinner());
    }
    @Test
    void testFight_draw() {

        Card cardWaterSpell = new Card("1ab", "WaterGoblin", 10);
        Card cardWaterGoblin = new Card("2ab", "WaterGoblin", 10);
        String result = battleController.fight(cardWaterSpell, cardWaterGoblin);
        assertEquals("Draw (no action)\n", result);
        //verify that it is a draw
        assertEquals(0,battleController.getWinner());
    }


    @Test
    void testResolveMonsterFight_KrakenImmune_Awins() {
        Card cardA = new Card("1", "Kraken", 10);
        Card cardB = new Card("2", "Wizard", 8);
        String result = battleController.resolveMonsterFight(cardA, cardB);
        assertEquals("The Kraken is immune against spells.\n", result);
        assertEquals(1 , battleController.getWinner());
    }

    @Test
    void testResolveSpellFight_WaterBeatsFire_sameDmg_PlayerBWins() {

        Card cardFireSpell = new Card("5ab", "FireSpell", 10);
        Card cardWaterSpell = new Card("6ab", "WaterSpell", 10);

        String result = battleController.resolveSpellFight(cardFireSpell, cardWaterSpell);
        assertEquals("PlayerB: WaterSpell wins\n", result);
        //Veryfy PlayerB Won
        assertEquals(2 , battleController.getWinner());
    }

    @Test
    void testResolveSpellFight_WaterBeatsFire_fireHigherDmg() {
        Card cardFireSpell = new Card("5ab", "FireSpell", 15);
        Card cardWaterSpell = new Card("6ab", "WaterSpell", 10);

        String result = battleController.resolveSpellFight(cardFireSpell, cardWaterSpell);
        assertEquals("PlayerB: WaterSpell wins\n", result);
        assertEquals(2 , battleController.getWinner());
    }

    @Test
    void testResolveMixedFight() {
        Card cardKnight = new Card("3ab", "Knight", 2);
        Card carWaterSpell = new Card("5ab", "WaterSpell", 10);
        String result = battleController.resolveMixedFight(cardKnight, carWaterSpell);
        assertEquals("The armor of Knights is so heavy that WaterSpells make them drown them instantly.\n", result);
    }


    @Test
    public void testResolveMonsterFight_KrakenVsDragon() {
        Card cardKraken = new Card("4ab", "Kraken", 10);
        Card cardDragon = new Card("1ab", "Dragon", 10);
        assertEquals("The Kraken is immune against spells.\n", battleController.fight(cardKraken, cardDragon));
    }

    @Test
    public void testResolveMonsterFight_DragonVsGoblin() {
        Card cardDragon = new Card("1ab", "Dragon", 10);
        Card cardWaterGoblin = new Card("2ab", "WaterGoblin", 10);
        assertEquals("The Dragon is not able to damage the WaterGoblin.\n", battleController.fight(cardDragon, cardWaterGoblin));
    }
    @Test
    void resolveSpellFight_fireBeatsWater_sameDmg_PlayerAWins() {
        // Arrange
        Card cardFireSpell = new Card("5ab", "FireSpell", 10);
        Card cardWaterSpell = new Card("6ab", "WaterSpell", 10);

        // Act
        String result = battleController.resolveSpellFight(cardFireSpell, cardWaterSpell);

        // Assert
        assertEquals("PlayerB: WaterSpell wins\n", result);
        // Verify PlayerA won
        assertEquals(2 , battleController.getWinner());
    }

    @Test
    void resolveMixedFight_KnightVsFireSpell() {
        // Arrange
        Card cardKnight = new Card("3ab", "Knight", 2);
        Card cardFireSpell = new Card("5ab", "FireSpell", 10);

        // Act
        String result = battleController.resolveMixedFight(cardKnight, cardFireSpell);

        // Assert
        assertEquals("The Kraken is immune against spells.\n", result);
    }

}
