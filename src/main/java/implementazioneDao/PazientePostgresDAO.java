package implementazioneDao;

import dao.PazienteDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia PazienteDAO per la gestione dei pazienti
 * su un database PostgreSQL.
 */
public class PazientePostgresDAO implements PazienteDAO {

    private static final String COLUMNS = "cf, nome, cognome, data_nascita, sesso, residenza, diagnosi";
    private static final String AGGIUNGI_PAZIENTE_QUERY = "INSERT INTO paziente (nome, cognome, cf, data_nascita, sesso, residenza, diagnosi) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_PAZIENTE_BY_CF_QUERY = "SELECT " + COLUMNS + " FROM paziente WHERE cf = ?";
    private static final String GET_ALL_PAZIENTI_QUERY = "SELECT " + COLUMNS + " FROM paziente ORDER BY cognome, nome";
    private static final String AGGIORNA_PAZIENTE_QUERY = "UPDATE paziente SET nome = ?, cognome = ?, data_nascita = ?, sesso = ?, residenza = ?, diagnosi = ? WHERE cf = ?";
    private static final String ELIMINA_PAZIENTE_QUERY = "DELETE FROM paziente WHERE cf = ?";
    private static final Logger LOGGER = Logger.getLogger(PazientePostgresDAO.class.getName());

    /**
     * {@inheritDoc}
     * Aggiunge un nuovo paziente al database.
     *
     * @param cf          Il codice fiscale del paziente (chiave primaria).
     * @param nome        Il nome del paziente.
     * @param cognome     Il cognome del paziente.
     * @param dataNascita La data di nascita (formato "AAAA-MM-GG").
     * @param sesso       Il sesso del paziente (es. 'M' o 'F').
     * @param residenza   L'indirizzo di residenza.
     * @param diagnosi    La diagnosi iniziale.
     */
    @Override
    public boolean aggiungiPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_PAZIENTE_QUERY)) {
             
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, cf);
            stmt.setDate(4, java.sql.Date.valueOf(dataNascita));
            stmt.setString(5, sesso);
            stmt.setString(6, residenza);
            stmt.setString(7, diagnosi);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del paziente nel database", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * Recupera un paziente specifico dal database tramite il suo codice fiscale.
     *
     * @param cf Il codice fiscale del paziente da cercare.
     */
    @Override
    public ArrayList<String> getPazienteByCf(String cf) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_PAZIENTE_BY_CF_QUERY)) {
             
            stmt.setString(1, cf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractPazienteFromResultSet(rs);
                }
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero del paziente dal database", e);
        }
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * Recupera tutti i pazienti dal database, ordinati per cognome e nome.
     */
    @Override
    public ArrayList<ArrayList<String>> getAllPazienti() {
        ArrayList<ArrayList<String>> paziente = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_PAZIENTI_QUERY);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                paziente.add(extractPazienteFromResultSet(rs));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero di tutti i pazienti dal database", e);
        }
        return paziente;
    }
    
    /**
     * {@inheritDoc}
     * Aggiorna i dati di un paziente esistente.
     *
     * @param cf          Il codice fiscale del paziente da aggiornare.
     * @param nome        Il nuovo nome.
     * @param cognome     Il nuovo cognome.
     * @param dataNascita La nuova data di nascita.
     * @param sesso       Il nuovo sesso.
     * @param residenza   La nuova residenza.
     * @param diagnosi    La nuova diagnosi.
     */
    @Override
    public boolean aggiornaPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_PAZIENTE_QUERY)) {

            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setDate(3, java.sql.Date.valueOf(dataNascita));
            stmt.setString(4, sesso);
            stmt.setString(5, residenza);
            stmt.setString(6, diagnosi);
            stmt.setString(7, cf);

            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento del paziente nel database", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * Elimina un paziente dal database.
     *
     * @param cf Il codice fiscale del paziente da eliminare.
     */
    @Override
    public boolean eliminaPaziente(String cf) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(ELIMINA_PAZIENTE_QUERY)) {
            stmt.setString(1, cf);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione del paziente dal database", e);
        }
        return false;
    }

    /**
     * Metodo helper per estrarre i dati di un paziente da un ResultSet.
     *
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe contenente i dati del paziente.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati.
     */
    private ArrayList<String> extractPazienteFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> paziente = new ArrayList<>();
        paziente.add(rs.getString("cf"));
        paziente.add(rs.getString("nome"));
        paziente.add(rs.getString("cognome"));
        java.sql.Date dataDb = rs.getDate("data_nascita");
        paziente.add(dataDb != null ? dataDb.toString() : "");
        paziente.add(rs.getString("sesso"));
        paziente.add(rs.getString("residenza"));
        paziente.add(rs.getString("diagnosi"));
        return paziente;
    }
}