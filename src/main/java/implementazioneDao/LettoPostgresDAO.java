package implementazioneDao;

import dao.LettoDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia LettoDAO per la gestione dei letti
 * su un database PostgreSQL.
 */
public class LettoPostgresDAO implements LettoDAO {

    private static final Logger LOGGER = Logger.getLogger(LettoPostgresDAO.class.getName());
    
    private static final String AGGIUNGI_LETTO_QUERY = "INSERT INTO letto (numero_letto, reparto_di_appartenenza, is_libero, num_stanza) VALUES (?, ?, true, 'NON SPECIFICATA')";
    private static final String GET_LETTO_BY_ID_QUERY = "SELECT numero_letto, reparto_di_appartenenza, is_libero, num_stanza FROM letto WHERE numero_letto = ? AND reparto_di_appartenenza = ?";
    private static final String GET_ALL_LETTI_QUERY = "SELECT numero_letto, reparto_di_appartenenza, is_libero, num_stanza FROM letto ORDER BY reparto_di_appartenenza, num_stanza, numero_letto";
    private static final String AGGIORNA_STATO_LETTO_QUERY = "UPDATE letto SET is_libero = ? WHERE numero_letto = ? AND reparto_di_appartenenza = ?";
    private static final String GET_ALL_REPARTI_QUERY = "SELECT DISTINCT reparto_di_appartenenza FROM letto WHERE reparto_di_appartenenza IS NOT NULL AND reparto_di_appartenenza <> '' ORDER BY reparto_di_appartenenza ASC";
    private static final String COL_NUMERO_LETTO = "numero_letto";
    private static final String COL_REPARTO_APPARTENENZA = "reparto_di_appartenenza";
    private static final String COL_IS_LIBERO = "is_libero";
    private static final String COL_NUM_STANZA = "num_stanza";

    /**
     * Aggiunge un nuovo letto al database.
     *
     * @param idLetto L'identificativo del letto.
     * @param reparto Il reparto di appartenenza.
     * @return {@code true} se il letto è stato aggiunto con successo, altrimenti {@code false}.
     */
    @Override
    public boolean aggiungiLetto(String idLetto, String reparto) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_LETTO_QUERY)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            return stmt.executeUpdate() > 0; // Restituisce true se l'inserimento va a buon fine
        } catch (SQLException | NullPointerException e) { // Aggiunto NullPointerException
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del letto", e);
        }
        return false;
    }

    /**
     * Recupera un letto specifico dal database tramite il suo ID e reparto.
     *
     * @param idLetto L'identificativo del letto.
     * @param reparto Il reparto di appartenenza.
     * @return un'ArrayList di stringhe con i dati del letto, o una lista vuota se non trovato.
     */
    @Override
    public ArrayList<String> getLettoById(String idLetto, String reparto) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_LETTO_BY_ID_QUERY)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractLettoFromResultSet(rs);
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del letto per ID", e);
        }
        return new ArrayList<>();
    }

    /**
     * Recupera tutti i letti presenti nel database.
     *
     * @return una lista di tutti i letti, dove ogni letto è rappresentato da un'ArrayList di stringhe.
     */
    @Override
    public ArrayList<ArrayList<String>> getAllLetti() {
        ArrayList<ArrayList<String>> letti = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_LETTI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                letti.add(extractLettoFromResultSet(rs));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutti i letti", e);
        }
        return letti;
    }

    /**
     * Aggiorna lo stato di occupazione di un letto.
     *
     * @param idLetto  L'identificativo del letto.
     * @param reparto  Il reparto di appartenenza.
     * @param occupato {@code true} se il letto è occupato, {@code false} se è libero.
     * @return {@code true} se l'aggiornamento ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean aggiornaStatoLetto(String idLetto, String reparto, boolean occupato) {
        boolean isLibero = !occupato; // La logica è invertita: se è occupato, non è libero.
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_STATO_LETTO_QUERY)) {
            stmt.setBoolean(1, isLibero);
            stmt.setString(2, idLetto);
            stmt.setString(3, reparto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dello stato del letto", e);
        }
        return false;
    }

    /**
     * Recupera una lista di tutti i nomi dei reparti distinti presenti nel database.
     *
     * @return una lista di stringhe contenente i nomi dei reparti.
     */
    @Override
    public List<String> getAllReparti() {
        List<String> reparti = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_REPARTI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reparti.add(rs.getString(COL_REPARTO_APPARTENENZA));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei reparti", e);
        }
        return reparti;
    }

    /**
     * Metodo helper per estrarre i dati di un letto da un ResultSet.
     *
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe contenente i dati del letto.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati.
     */
    private ArrayList<String> extractLettoFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> letto = new ArrayList<>();
        boolean isLibero = rs.getBoolean(COL_IS_LIBERO);
        letto.add(rs.getString(COL_NUMERO_LETTO));
        letto.add(rs.getString(COL_REPARTO_APPARTENENZA));
        letto.add(String.valueOf(!isLibero)); // Convertiamo is_libero in 'occupato'
        letto.add(rs.getString(COL_NUM_STANZA));
        return letto;
    }
}