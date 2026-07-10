package implementazioneDao;

import dao.DimissioniDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia DimissioniDAO per la gestione delle dimissioni dei pazienti
 * su un database PostgreSQL.
 */
public class DimissioniPostgresDAO implements DimissioniDAO {

    private static final Logger LOGGER = Logger.getLogger(DimissioniPostgresDAO.class.getName());

    private static final String CREA_DIMISSIONE_QUERY = "UPDATE ricovero SET data_fine = ?, prognosi = ?, esito = ? WHERE id_ricovero = ?";
    private static final String GET_ALL_DIMISSIONI_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE data_fine IS NOT NULL ORDER BY data_fine DESC";
    private static final String GET_ULTIMO_RICOVERO_CHIUSO_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE cf = ? AND data_fine IS NOT NULL ORDER BY data_fine DESC LIMIT 1";
    private static final String ELIMINA_DIMISSIONE_QUERY = "DELETE FROM ricovero WHERE id_ricovero = ?";

    private static final String COL_ID_RICOVERO = "id_ricovero";
    private static final String COL_CF = "cf";
    private static final String COL_ID_LETTO = "id_letto";
    private static final String COL_REPARTO = "reparto";
    private static final String COL_DATA_INIZIO = "data_inizio";
    private static final String COL_MOTIVAZIONE = "motivazione";
    private static final String COL_DATA_FINE = "data_fine";
    private static final String COL_PROGNOSI = "prognosi";
    private static final String COL_ESITO = "esito";

    /**
     * Crea una dimissione aggiornando un ricovero esistente con la data di fine e altri dettagli.
     *
     * @param idRicovero L'ID del ricovero da chiudere.
     * @param dataFine   La data e l'ora di fine del ricovero.
     * @param prognosi   La prognosi in giorni.
     * @param esito      L'esito della dimissione (es. "Ordinaria", "Decesso").
     * @return {@code true} se la dimissione è stata creata con successo, altrimenti {@code false}.
     */
    @Override
    public boolean creaDimissione(String idRicovero, String dataFine, String prognosi, String esito) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(CREA_DIMISSIONE_QUERY)) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(dataFine));
            stmt.setString(2, prognosi);
            stmt.setString(3, esito);
            stmt.setInt(4, Integer.parseInt(idRicovero));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante la creazione della dimissione per idRicovero: " + idRicovero);
        }
        return false;
    }

    /**
     * Recupera tutte le dimissioni (ricoveri chiusi) dal database.
     *
     * @return una lista di tutte le dimissioni, ordinate per data di fine decrescente.
     */
    @Override
    public ArrayList<ArrayList<String>> getAllDimissioni() {
        ArrayList<ArrayList<String>> dimissioni = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_DIMISSIONI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dimissioni.add(extractDimissioneFromResultSet(rs));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutte le dimissioni", e); // Questo era già corretto, ma lo lascio per coerenza
        }
        return dimissioni;
    }

    /**
     * Recupera l'ultimo ricovero chiuso per un paziente specifico.
     *
     * @param cfPaziente Il codice fiscale del paziente.
     * @return un'ArrayList di stringhe con i dati dell'ultima dimissione, o una lista vuota se non trovata.
     */
    @Override
    public ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ULTIMO_RICOVERO_CHIUSO_QUERY)) {
            stmt.setString(1, cfPaziente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractDimissioneFromResultSet(rs);
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante il recupero dell'ultimo ricovero chiuso per il paziente " + cfPaziente);
        }
        return new ArrayList<>();
    }

    /**
     * Elimina una dimissione (e il relativo ricovero) dal database.
     *
     * @param idRicovero L'ID del ricovero da eliminare.
     * @return {@code true} se l'eliminazione ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean eliminaDimissione(String idRicovero) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(ELIMINA_DIMISSIONE_QUERY)) {
            stmt.setInt(1, Integer.parseInt(idRicovero));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NumberFormatException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante l'eliminazione della dimissione con id: " + idRicovero);
        }
        return false;
    }

    /**
     * Metodo helper per estrarre i dati di una dimissione da un ResultSet.
     *
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe contenente i dati della dimissione.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati.
     */
    private ArrayList<String> extractDimissioneFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> ricovero = new ArrayList<>();
        ricovero.add(String.valueOf(rs.getInt(COL_ID_RICOVERO)));
        ricovero.add(rs.getString(COL_CF));
        ricovero.add(rs.getString(COL_ID_LETTO));
        ricovero.add(rs.getString(COL_REPARTO));
        ricovero.add(String.valueOf(rs.getTimestamp(COL_DATA_INIZIO)));
        ricovero.add(String.valueOf(rs.getTimestamp(COL_DATA_FINE)));
        ricovero.add(rs.getString(COL_MOTIVAZIONE));
        ricovero.add(rs.getString(COL_PROGNOSI));
        ricovero.add(rs.getString(COL_ESITO));
        return ricovero;
    }
}