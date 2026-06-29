package database_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnessioneDatabase {

    private static final String URL = "jdbc:postgresql://localhost:5432/...";
    private static final String USER = "...";
    private static final String PASSWORD = "...";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Errore di connessione al DB: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}