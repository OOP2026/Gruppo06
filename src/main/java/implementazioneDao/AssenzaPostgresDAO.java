package implementazioneDao;

import dao.AssenzaDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia AssenzaDAO per la gestione delle assenze dei medici
 * su un database PostgreSQL.
 */
public class AssenzaPostgresDAO implements AssenzaDAO {

    private static final Logger LOGGER = Logger.getLogger(AssenzaPostgresDAO.class.getName());
    
    private static final String AGGIUNGI_ASSENZA_QUERY = "INSERT INTO assenza (matricola, data_inizio, data_fine, motivazione) VALUES (?, ?, ?, ?)";
    private static final String GET_ASSENZA_QUERY = "SELECT matricola, data_inizio, data_fine, motivazione FROM assenza WHERE matricola = ? AND data_inizio = ?";
    private static final String GET_ASSENZE_BY_MEDICO_QUERY = "SELECT matricola, data_inizio, data_fine, motivazione FROM assenza WHERE matricola = ? ORDER BY data_inizio ASC";
    private static final String AGGIORNA_ASSENZA_QUERY = "UPDATE assenza SET data_fine = ?, motivazione = ? WHERE matricola = ? AND data_inizio = ?";
    private static final String ELIMINA_ASSENZA_QUERY = "DELETE FROM assenza WHERE matricola = ? AND data_inizio = ?";

    // Costanti per i nomi delle colonne
    private static final String COL_MATRICOLA = "matricola";
    private static final String COL_DATA_INIZIO = "data_inizio";
    private static final String COL_DATA_FINE = "data_fine";
    private static final String COL_MOTIVAZIONE = "motivazione";

    /**
     * Aggiunge una nuova assenza per un medico nel database.
     *
     * @param matricola   La matricola del medico.
     * @param dataInizio  La data di inizio dell'assenza (formato "AAAA-MM-GG").
     * @param dataFine    La data di fine dell'assenza (formato "AAAA-MM-GG").
     * @param motivazione La motivazione dell'assenza.
     * @return {@code true} se l'assenza è stata aggiunta con successo, altrimenti {@code false}.
     */
    @Override
    public boolean aggiungiAssenza(String matricola, String dataInizio, String dataFine, String motivazione) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_ASSENZA_QUERY)) {
             
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(dataInizio));
            stmt.setDate(3, java.sql.Date.valueOf(dataFine));
            stmt.setString(4, motivazione);

            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta dell'assenza", e);
        }
        return false;
    }

    /**
     * Recupera una specifica assenza dal database.
     *
     * @param matricola  La matricola del medico.
     * @param dataInizio La data di inizio dell'assenza da cercare.
     * @return un'ArrayList di stringhe con i dati dell'assenza, o una lista vuota se non trovata.
     */
    @Override
    public ArrayList<String> getAssenza(String matricola, String dataInizio) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ASSENZA_QUERY)) {
             
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(dataInizio));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractAssenzaFromResultSet(rs);
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dell'assenza", e);
        }
        return new ArrayList<>();
    }

    /**
     * Recupera tutte le assenze registrate per un dato medico.
     *
     * @param matricola La matricola del medico.
     * @return una lista di tutte le assenze del medico, ordinate per data di inizio.
     */
    @Override
    public ArrayList<ArrayList<String>> getAssenzeByMedico(String matricola) {
        ArrayList<ArrayList<String>> assenze = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ASSENZE_BY_MEDICO_QUERY)) {
             
            stmt.setString(1, matricola);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assenze.add(extractAssenzaFromResultSet(rs));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle assenze del medico", e);
        }
        return assenze;
    }
    /**
     * Aggiorna i dettagli di un'assenza esistente.
     *
     * @param matricola   La matricola del medico.
     * @param dataInizio  La data di inizio originale dell'assenza (usata come chiave).
     * @param dataFine    La nuova data di fine dell'assenza.
     * @param motivazione La nuova motivazione.
     * @return {@code true} se l'aggiornamento ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean aggiornaAssenza(String matricola, String dataInizio, String dataFine, String motivazione) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_ASSENZA_QUERY)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataFine));
            stmt.setString(2, motivazione);
            stmt.setString(3, matricola);
            stmt.setDate(4, java.sql.Date.valueOf(dataInizio));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'assenza", e);
        }
        return false;
    }

    /**
     * Elimina un'assenza dal database.
     * @param matricola La matricola del medico.
     * @param dataInizio La data di inizio dell'assenza da eliminare.
     * @return {@code true} se l'eliminazione ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean eliminaAssenza(String matricola, String dataInizio) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(ELIMINA_ASSENZA_QUERY)) {
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(dataInizio));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione dell'assenza", e);
        }
        return false;
    }

    /**
     * Metodo helper per estrarre i dati di un'assenza da un ResultSet.
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe contenente i dati dell'assenza.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati.
     */
    private ArrayList<String> extractAssenzaFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> assenza = new ArrayList<>();
        assenza.add(rs.getString(COL_MATRICOLA));

        java.sql.Date dataInizioDb = rs.getDate(COL_DATA_INIZIO);
        assenza.add(dataInizioDb != null ? dataInizioDb.toString() : "");

        java.sql.Date dataFineDb = rs.getDate(COL_DATA_FINE);
        assenza.add(dataFineDb != null ? dataFineDb.toString() : "");

        assenza.add(rs.getString(COL_MOTIVAZIONE));
        return assenza;
    }
}