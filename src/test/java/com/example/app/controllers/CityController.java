package com.example.app.controllers;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CityController {
    @BeforeAll
    void beforeAll() {
        System.out.println("Running before all tests");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Running before each test");
    }

    @AfterEach
    void afterEach() {
        System.out.println("Running after each test");
    }

    @AfterAll
    void afterAll() {
        System.out.println("Running after all tests");
    }

    @Test
    void showCaseTest() {
        // A - arrange, given
        int a = 1;
        int b = 2;
        int expectedResult = 3;

        System.out.println("Running during the test");
        // A - act, when
        int actualResult = a + b;

        // A - assert, then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void showCaseTest2() {
        // A - arrange, given
        int a = 1;
        int b = 2;
        int expectedResult = 3;

        System.out.println("Running during the test 2");
        // A - act, when
        int actualResult = a + b;

        // A - assert, then
        assertEquals(expectedResult, actualResult);
    }
}
