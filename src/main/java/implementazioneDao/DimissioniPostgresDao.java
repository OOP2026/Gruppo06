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

public class DimissioniPostgresDao implements DimissioniDAO {

    private static final Logger LOGGER = Logger.getLogger(DimissioniPostgresDao.class.getName());

    private static final String CREA_DIMISSIONE_QUERY = "UPDATE ricovero SET data_fine = ?, prognosi = ?, esito = ? WHERE id_ricovero = ?";
    private static final String GET_ALL_DIMISSIONI_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE data_fine IS NOT NULL ORDER BY data_fine DESC";
    private static final String GET_ULTIMO_RICOVERO_CHIUSO_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE cf = ? AND data_fine IS NOT NULL ORDER BY data_fine DESC LIMIT 1";
    private static final String ELIMINA_DIMISSIONE_QUERY = "DELETE FROM ricovero WHERE id_ricovero = ?";

    // Costanti per i nomi delle colonne
    private static final String COL_ID_RICOVERO = "id_ricovero";
    private static final String COL_CF = "cf";
    private static final String COL_ID_LETTO = "id_letto";
    private static final String COL_REPARTO = "reparto";
    private static final String COL_DATA_INIZIO = "data_inizio";
    private static final String COL_MOTIVAZIONE = "motivazione";
    private static final String COL_DATA_FINE = "data_fine";
    private static final String COL_PROGNOSI = "prognosi";
    private static final String COL_ESITO = "esito";

    @Override
    public boolean creaDimissione(String idRicovero, String dataFine, String prognosi, String esito) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREA_DIMISSIONE_QUERY)) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(dataFine));
            stmt.setString(2, prognosi);
            stmt.setString(3, esito);
            stmt.setInt(4, Integer.parseInt(idRicovero));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la creazione della dimissione", e);
        }
        return false;
    }

    @Override
    public ArrayList<ArrayList<String>> getAllDimissioni() {
        ArrayList<ArrayList<String>> dimissioni = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_DIMISSIONI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
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
                dimissioni.add(ricovero);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutte le dimissioni", e);
        }
        return dimissioni;
    }

    @Override
    public ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ULTIMO_RICOVERO_CHIUSO_QUERY)) {
            stmt.setString(1, cfPaziente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dell'ultimo ricovero chiuso per il paziente " + cfPaziente, e);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean eliminaDimissione(String idRicovero) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ELIMINA_DIMISSIONE_QUERY)) {
            stmt.setInt(1, Integer.parseInt(idRicovero));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione della dimissione", e);
        }
        return false;
    }
}