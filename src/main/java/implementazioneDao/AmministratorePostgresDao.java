package implementazioneDao;
 
import dao.AmministratoreDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class AmministratorePostgresDao implements AmministratoreDAO {
 
        private static final Logger LOGGER = Logger.getLogger(AmministratorePostgresDao.class.getName());
        private static final String CHECK_LOGIN_ESISTENTE_QUERY = "SELECT 1 FROM amministratore WHERE login = ?";
        private static final String AGGIUNGI_AMMINISTRATORE_QUERY = "INSERT INTO amministratore (matricola, login, password, nome, cognome, pin) VALUES (?, ?, ?, ?, ?, ?)";
        private static final String GET_AMMINISTRATORE_BY_LOGIN_AND_PASSWORD_QUERY = "SELECT nome, cognome, login, password, matricola, pin FROM amministratore WHERE login = ? AND password = ?";
 
        @Override
        public boolean checkLoginEsistente(String login) {
        try (Connection conn = ConnessioneDatabase.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(CHECK_LOGIN_ESISTENTE_QUERY)) {
 
                stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

            } catch (SQLException | NullPointerException e) {
                LOGGER.log(Level.SEVERE, "Errore durante la verifica del login", e);
            }
            return false;
        }
 
    @Override
    public boolean aggiungiAmministratore(String matricola, String login, String password, String nome, String cognome, String pin) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_AMMINISTRATORE_QUERY)) {
            stmt.setString(1, matricola);
            stmt.setString(2, login);
            stmt.setString(3, password);
            stmt.setString(4, nome);
            stmt.setString(5, cognome);
            stmt.setString(6, pin);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento dell'amministratore nel DB", e);
            return false;
        }
    }
 
 
    @Override
        public ArrayList<String> getAmministratoreByLoginAndPassword(String login, String password) {
        try (Connection conn = ConnessioneDatabase.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(GET_AMMINISTRATORE_BY_LOGIN_AND_PASSWORD_QUERY)) {
 
                stmt.setString(1, login);
                stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAmministratoreFromResultSet(rs);
                }
            }
            } catch (SQLException | NullPointerException e) {
                LOGGER.log(Level.SEVERE, "Errore durante il recupero dell'amministratore per login e password", e);
            }
            return new ArrayList<>();
        }

    private ArrayList<String> extractAmministratoreFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> datiAmministratore = new ArrayList<>();
        datiAmministratore.add(rs.getString("nome"));
        datiAmministratore.add(rs.getString("cognome"));
        datiAmministratore.add(rs.getString("login"));
        datiAmministratore.add(rs.getString("password"));
        datiAmministratore.add(rs.getString("matricola"));
        datiAmministratore.add(rs.getString("pin"));
        return datiAmministratore;
    }
}
