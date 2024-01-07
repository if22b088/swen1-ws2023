package example.com.app;

import example.com.app.controllers.CityController;

import example.com.app.controllers.UserController;
import example.com.app.daos.UserDAO;
import example.com.app.repositories.UserRepository;

import example.com.app.controllers.CardController;
import example.com.app.daos.CardDAO;
import example.com.app.repositories.CardRepository;

import example.com.app.controllers.PackageController;
import example.com.app.daos.PackageDAO;
import example.com.app.repositories.PackageRepository;

import example.com.app.controllers.TradingController;
import example.com.app.daos.TradingDAO;
import example.com.app.repositories.TradingRepository;


import example.com.app.services.DatabaseService;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Setter;
import example.com.server.Request;
import example.com.server.Response;
import example.com.server.ServerApp;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;


public class App implements ServerApp {
    /*
    @Setter(AccessLevel.PRIVATE)
    private CityController cityController;
     */
    @Setter(AccessLevel.PRIVATE)
    private UserController userController;
    @Setter(AccessLevel.PRIVATE)
    private CardController cardController;
    @Setter(AccessLevel.PRIVATE)
    private PackageController packageController;
    @Setter(AccessLevel.PRIVATE)
    private TradingController tradingController;


    public App() {

        DatabaseService databaseService = new DatabaseService();

        /*
        CityDAO cityDAO = new CityDAO(databaseService.getConnection());
        CityRepository cityRepository = new CityRepository(cityDAO);
        setCityController(new CityController(cityRepository));
        */
        UserDAO userDAO = new UserDAO(databaseService.getConnection());
        UserRepository userRepository = new UserRepository(userDAO);
        setUserController(new UserController(userRepository));

        CardDAO cardDAO = new CardDAO(databaseService.getConnection());
        CardRepository cardRepository = new CardRepository(cardDAO);
        setCardController(new CardController(cardRepository));

        PackageDAO packageDAO = new PackageDAO(databaseService.getConnection());
        PackageRepository packageRepository = new PackageRepository(packageDAO);
        setPackageController(new PackageController(cardRepository,userRepository,packageRepository));
    }

    public Response handleRequest(Request request) {

        switch (request.getMethod()) {
            //get user information by username
            case GET: {
                if (request.getPathname().startsWith("/users/")) {
                    //get the username from the path
                    String source = request.getPathname();
                    String target = "/users/";
                    String replacement = "";
                    String username= source.replace(target, replacement);
                    return this.userController.getUserByUsername(username,request.getToken());
                } else if (request.getPathname().equals("/cards")) {
                    return this.cardController.getCards(request.getToken());
                } else if (request.getPathname().equals("/deck") || request.getPathname().equals("/deck?format=plain")) {
                    //get the format from request parameters
                    String source = request.getParams();
                    String target = "format=";
                    String replacement = "";
                    String format= source.replace(target, replacement);
                    System.out.println("this is the format: " + format);
                    return this.cardController.getDeck(request.getToken(),format);
                } else if (request.getPathname().equals("/stats")) {
                    return this.userController.getStats(request.getToken());
                } else if (request.getPathname().equals("/scoreboard")) {
                    return this.userController.getScoreboard(request.getToken());
                } else if (request.getPathname().equals("/tradings")) {
                    return this.tradingController.getTradings(request.getToken());
                }


            }
            case POST: {
                //create User
                if (request.getPathname().equals("/users")) {
                    String body = request.getBody();
                    return this.userController.createUser(body);
                //login User
                } else if (request.getPathname().equals("/sessions")) {
                    String body = request.getBody();
                    return this.userController.loginUser(body);
                //create package
                } else if (request.getPathname().equals("/packages")) {
                    String body = request.getBody();
                    return this.packageController.createPackage(body, request.getToken());
                //buy package
                } else if (request.getPathname().equals("/transactions/packages")) {
                    String body = request.getBody();
                    return this.packageController.buyPackage(body, request.getToken());
                } else if (request.getPathname().equals("/tradings")) {
                    String body = request.getBody();
                    return this.tradingController.createTrading(body,request.getToken());
                } else if (request.getPathname().startsWith("/tradings/")) {
                    String body = request.getBody();
                    //get the username from the path
                    String master = request.getPathname();
                    String target = "/tradings/";
                    String replacement = "";
                    String tradingDealID= master.replace(target, replacement);

                    return this.tradingController.carryOutTrading(body, tradingDealID,request.getToken());
                }


                /*else if (request.getPathname().equals("/battles")) {
                    String body = request.getBody();
                    return this.cityController.createCity(body);
                }
                break;
                */
                //TODO: add GET for /tradings/{tradingdealid}

            }
            case PUT:
                //update name, bio, image for specific username
                if (request.getPathname().startsWith("/users/")) {
                    //get the username from the path
                    String master = request.getPathname();
                    String target = "/users/";
                    String replacement = "";
                    String username= master.replace(target, replacement);

                    return this.userController.updateUser(username,request.getBody(),request.getToken());
                    /*
                    String body = request.getBody();
                    return this.userController.updateUser(body);
                    */
                } else if (request.getPathname().equals("/deck")) {
                    String body = request.getBody();
                    return this.cardController.updateDeck(body,request.getToken());
                }
                break;
            case DELETE:
                if (request.getPathname().startsWith("/tradings/")) {
                    //get the username from the path
                    String master = request.getPathname();
                    String target = "/tradings/";
                    String replacement = "";
                    String tradingDealID= master.replace(target, replacement);

                    return this.tradingController.deleteTrading(tradingDealID,request.getToken());
                    /*
                    String body = request.getBody();
                    return this.userController.updateUser(body);
                    */
                }
                break;
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Not Found\", \"data\": null }");
    }
}
