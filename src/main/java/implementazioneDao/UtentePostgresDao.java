package implementazioneDao;

import dao.UtenteDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtentePostgresDao implements UtenteDAO{

        private static final Logger LOGGER = Logger.getLogger(UtentePostgresDao.class.getName());

        @Override
        public boolean checkLoginEsistente(String login) {
        String query = "SELECT 1 FROM amministratore WHERE login = ?";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, login);
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // Ritorna true se trova una corrispondenza

            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Errore durante la verifica del login", e);
            }
            return false;
        }

    @Override
    public boolean aggiungiUtente(String matricola, String login, String password, String nome, String cognome, String ruolo) {
        String query = "INSERT INTO amministratore (matricola, login, password, nome, cognome) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricola);
            stmt.setString(2, login);
            stmt.setString(3, password);
            stmt.setString(4, nome);
            stmt.setString(5, cognome);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento dell'utente nel DB", e);
            return false;
        }
    }


    @Override
        public ArrayList<String> getUtenteByLoginAndPassword(String login, String password) {
        String query = "SELECT nome, cognome, login, password, matricola FROM amministratore WHERE login = ? AND password = ?";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, login);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    ArrayList<String> datiUtente = new ArrayList<>();
                    // L'ordine è importante per il Controller: nome, cognome, login, password, matricola
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");

                datiUtente.add(nome);
                datiUtente.add(cognome);
                    datiUtente.add(rs.getString("login"));
                    datiUtente.add(rs.getString("password"));
                    datiUtente.add(rs.getString("matricola"));
                    
                    return datiUtente;
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Errore durante il recupero dell'utente per login e password", e);
            }
            return new ArrayList<>();
        }
}
