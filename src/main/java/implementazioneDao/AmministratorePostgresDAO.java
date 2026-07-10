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
 
/**
 * Implementazione dell'interfaccia AmministratoreDAO per la gestione degli amministratori
 * su un database PostgreSQL.
 */
public class AmministratorePostgresDAO implements AmministratoreDAO {
 
        private static final Logger LOGGER = Logger.getLogger(AmministratorePostgresDAO.class.getName());
        private static final String CHECK_LOGIN_ESISTENTE_QUERY = "SELECT 1 FROM amministratore WHERE login = ?";
        private static final String AGGIUNGI_AMMINISTRATORE_QUERY = "INSERT INTO amministratore (matricola, login, password, nome, cognome, pin) VALUES (?, ?, ?, ?, ?, ?)";
        private static final String GET_AMMINISTRATORE_BY_LOGIN_AND_PASSWORD_QUERY = "SELECT nome, cognome, login, password, matricola, pin FROM amministratore WHERE login = ? AND password = ?";
        private static final String UPDATE_ADMIN_QUERY = "UPDATE amministratore SET nome = ?, cognome = ? WHERE matricola = ?";

        /**
         * Verifica se un login (username) esiste già nel database.
         * @param login il login da verificare.
         * @return {@code true} se il login esiste già, altrimenti {@code false}.
         */
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
 
    /**
     * Aggiunge un nuovo amministratore al database.
     * @param matricola la matricola del nuovo amministratore.
     * @param login il login (username) del nuovo amministratore.
     * @param password la password del nuovo amministratore.
     * @param nome il nome del nuovo amministratore.
     * @param cognome il cognome del nuovo amministratore.
     * @param pin il PIN di sicurezza del nuovo amministratore.
     * @return {@code true} se l'aggiunta ha avuto successo, altrimenti {@code false}.
     */
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
 
 
    /**
     * Recupera i dati di un amministratore tramite login e password.
     * @param login il login dell'amministratore.
     * @param password la password dell'amministratore.
     * @return un'ArrayList di stringhe con i dati dell'amministratore, o una lista vuota se non trovato.
     */
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

    /**
     * Estrae i dati di un amministratore da un ResultSet.
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe con i dati dell'amministratore.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati del ResultSet.
     */
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

    /**
     * Aggiorna i dati (nome e cognome) di un amministratore esistente.
     * @param matricola la matricola dell'amministratore da aggiornare.
     * @param nome il nuovo nome.
     * @param cognome il nuovo cognome.
     * @return {@code true} se l'aggiornamento ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean aggiornaAmministratore(String matricola, String nome, String cognome) {
            try (Connection conn = ConnessioneDatabase.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(UPDATE_ADMIN_QUERY)) {
                stmt.setString(1, nome);
                stmt.setString(2, cognome);
                stmt.setString(3, matricola);
                return stmt.executeUpdate() > 0;
    } catch (SQLException | NullPointerException e) {
                LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'amministratore", e);
                return false;
            }
        }
}
