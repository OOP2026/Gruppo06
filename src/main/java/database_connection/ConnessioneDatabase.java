package database_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe di utilità per la gestione della connessione al database PostgreSQL.
 * Questa classe non può essere istanziata né estesa.
 */
public final class ConnessioneDatabase {
    private static final String URL = "jdbc:postgresql://localhost:5432/OspedaleOOP";
    private static final String USER = "postgres";
    private static final String PASSWORD = ""; //RIMUOVO PER PROBLEMI DI CODESMELLS - SICUREZZA

    /**
     * Impedisce l'istanziazione della classe di utilità.
     */
    private ConnessioneDatabase() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}