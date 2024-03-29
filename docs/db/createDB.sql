CREATE TABLE Cards (
    CardID VARCHAR(255) PRIMARY KEY,
    CardName VARCHAR(255),
    CardType VARCHAR(255),
    Damage INT
);


CREATE TABLE Decks (
    DeckID SERIAL PRIMARY KEY,
    Card1 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE,
    Card2 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE,
    Card3 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE,
    Card4 VARCHAR(255) REFERENCES Cards(CardID) UNIQUE
);


CREATE TABLE Users (
    UserID SERIAL PRIMARY KEY,
    Username VARCHAR(255) UNIQUE,
    Password VARCHAR(255),
    Name VARCHAR(255),
    Token VARCHAR(255) UNIQUE,
    Coins INT,
    DeckID INT REFERENCES Decks(DeckID) UNIQUE,
    Bio TEXT,
    Elo INT,
    Wins INT,
    Losses INT,
    Image VARCHAR(255)
);



CREATE TABLE Stacks (
    StackID SERIAL PRIMARY KEY,
    UserID INT REFERENCES Users(UserID),
    CardID VARCHAR(255) REFERENCES Cards(CardID)
);


CREATE TABLE Packages (
    PackageID SERIAL PRIMARY KEY,
    Card1 VARCHAR(255) REFERENCES Cards(CardID),
    Card2 VARCHAR(255) REFERENCES Cards(CardID),
    Card3 VARCHAR(255) REFERENCES Cards(CardID),
    Card4 VARCHAR(255) REFERENCES Cards(CardID),
    Card5 VARCHAR(255) REFERENCES Cards(CardID)
);


CREATE TABLE Tradings (
    UsernameOfferer VARCHAR(255),
    TradingID VARCHAR(255) PRIMARY KEY,
    CardToTrade VARCHAR(255) ,
    CardType VARCHAR(255),
    MinimumDamage INT
);

CREATE TABLE Battles (
    BattleID SERIAL PRIMARY KEY,
    User1 VARCHAR(255) REFERENCES Users(Username) UNIQUE,
    User2 VARCHAR(255) REFERENCES Users(Username),
    BattleLog TEXT 
);
