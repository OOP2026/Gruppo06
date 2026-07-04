package database_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnessioneDatabase {

    private static final String URL = "jdbc:postgresql://localhost:5432/OspedaleOOP";
    private static final String USER = "postgres"; 
    private static final String PASSWORD = "191123";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver di PostgreSQL non trovato.");
            throw new SQLException("Impossibile caricare il driver del database.", e);
        }
    }
}