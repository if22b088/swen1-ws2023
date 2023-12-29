package example.com.app;

import example.com.app.controllers.CityController;
import example.com.app.daos.CityDAO;
import example.com.app.repositories.CityRepository;

import example.com.app.controllers.UserController;
import example.com.app.daos.UserDAO;
import example.com.app.repositories.UserRepository;


import example.com.app.services.CityService;
import example.com.app.services.DatabaseService;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Setter;
import example.com.server.Request;
import example.com.server.Response;
import example.com.server.ServerApp;

import java.sql.Connection;


public class App implements ServerApp {
    @Setter(AccessLevel.PRIVATE)
    private CityController cityController;
    private UserController userController;

    public App() {
        DatabaseService databaseService = new DatabaseService();

        CityDAO cityDAO = new CityDAO(databaseService.getConnection());
        CityRepository cityRepository = new CityRepository(cityDAO);

        setCityController(new CityController(cityRepository));
    }

    public Response handleRequest(Request request) {

        switch (request.getMethod()) {
            //TODO: remove cityController
            case GET: {
                if (request.getPathname().startsWith("/users/")) {
                    //get the username from the path
                    String master = request.getPathname();
                    String target = "/users/";
                    String replacement = "";
                    String username= master.replace(target, replacement);

                    return this.userController.getUserByUsername(username);
                } /*else if (request.getPathname().equals("/cards")) {
                    return this.cardController.getUsers();
                } else if (request.getPathname().equals("/deck")) {
                    return this.deckController.getUsers();
                } else if (request.getPathname().equals("/stats")) {
                    return this.statController.getUsers();
                } else if (request.getPathname().equals("/scoreboard")) {
                    return this.scoreboardController.getUsers();
                } else if (request.getPathname().equals("/tradings")) {
                    return this.tradingController.getUsers();
                }
*/

            }
            case POST: {
                if (request.getPathname().equals("/cities")) {
                    String body = request.getBody();
                    return this.cityController.createCity(body);
                } else if (request.getPathname().equals("/users")) {
                    String body = request.getBody();
                    return this.cityController.createCity(body);
                } else if (request.getPathname().equals("/sessions")) {
                    String body = request.getBody();
                    return this.cityController.createCity(body);
                } else if (request.getPathname().equals("/packages")) {
                    String body = request.getBody();
                    return this.cityController.createCity(body);
                } else if (request.getPathname().equals("/transactions/packages")) {
                    String body = request.getBody();
                    return this.cityController.createCity(body);
                } else if (request.getPathname().equals("/battles")) {
                    String body = request.getBody();
                    return this.cityController.createCity(body);
                } else if (request.getPathname().equals("/tradings")) {
                    String body = request.getBody();
                    return this.cityController.createCity(body);
                }
                //TODO: add GET for /tradings/{tradingdealid}

            }
            case PUT:
                //TODO: add PUT for /users/{usersname}
                if (request.getPathname().equals("/users/")) {
                    return this.cityController.getCities();
                } else if (request.getPathname().equals("/deck")) {
                    return this.cityController.getCities();
                }

                break;
            case DELETE:
                //TODO: add PUT for /tradings/{tradingdealid}
                break;
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Not Found\", \"data\": null }");
    }
}
