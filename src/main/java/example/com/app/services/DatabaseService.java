package example.com.app.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {
    private String connectionString = "jdbc:postgresql://localhost:5432/users?user=postgres&password=postgres";
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Connection connection;

    public DatabaseService() {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            setConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
