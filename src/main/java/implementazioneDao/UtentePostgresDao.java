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
        
        private static final String CHECK_LOGIN_ESISTENTE_QUERY = "SELECT 1 FROM utente WHERE login = ?";
        private static final String AGGIUNGI_UTENTE_QUERY = "INSERT INTO utente (matricola, login, password, nome, cognome, ruolo) VALUES (?, ?, ?, ?, ?, ?)";
        private static final String GET_UTENTE_BY_LOGIN_AND_PASSWORD_QUERY = "SELECT nome, cognome, ruolo, login, password, matricola FROM utente WHERE login = ? AND password = ?";

        @Override
        public boolean checkLoginEsistente(String login) {
        try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(CHECK_LOGIN_ESISTENTE_QUERY)) {

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
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_UTENTE_QUERY)) {
            stmt.setString(1, matricola);
            stmt.setString(2, login);
            stmt.setString(3, password);
            stmt.setString(4, nome);
            stmt.setString(5, cognome);
            stmt.setString(6, ruolo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento dell'utente nel DB", e);
            return false;
        }
    }


    @Override
        public ArrayList<String> getUtenteByLoginAndPassword(String login, String password) {
        try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(GET_UTENTE_BY_LOGIN_AND_PASSWORD_QUERY)) {

                stmt.setString(1, login);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    ArrayList<String> datiUtente = new ArrayList<>();
                    // L'ordine è importante per il Controller: nome, cognome, ruolo, login, password, matricola
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");

                    datiUtente.add(nome);
                    datiUtente.add(cognome);
                    datiUtente.add(rs.getString("ruolo"));
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
