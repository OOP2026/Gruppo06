package implementazioneDao;

import dao.RicoveroDAO;
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
 * Implementazione dell'interfaccia RicoveroDAO per la gestione dei ricoveri dei pazienti
 * su un database PostgreSQL.
 */
public class RicoveroPostgresDAO implements RicoveroDAO {

    private static final Logger LOGGER = Logger.getLogger(RicoveroPostgresDAO.class.getName());
    private static final String AGGIUNGI_RICOVERO_QUERY = "INSERT INTO ricovero (cf, id_letto, reparto, data_inizio, motivazione) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_RICOVERO_ATTIVO_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, motivazione FROM ricovero WHERE cf = ? AND data_fine IS NULL ORDER BY data_inizio DESC LIMIT 1";
    private static final String GET_ALL_RICOVERI_ATTIVI_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, motivazione FROM ricovero WHERE data_fine IS NULL";
    private static final String IS_LETTO_ATTUALMENTE_OCCUPATO_QUERY = "SELECT 1 FROM ricovero WHERE id_letto = ? AND reparto = ? AND data_fine IS NULL LIMIT 1";
    private static final String GET_STORICO_RICOVERI_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE cf = ? ORDER BY data_inizio DESC";
    private static final String GET_STORICO_RICOVERI_BY_LETTO_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE id_letto = ? AND reparto = ? ORDER BY data_inizio DESC";
    private static final String AGGIORNA_LETTO_RICOVERO_QUERY = "UPDATE ricovero SET id_letto = ?, reparto = ? WHERE id_ricovero = ?";
    private static final String COL_ID_RICOVERO = "id_ricovero";
    private static final String COL_CF = "cf";
    private static final String COL_ID_LETTO = "id_letto";
    private static final String COL_REPARTO = "reparto";
    private static final String COL_DATA_INIZIO = "data_inizio";
    private static final String COL_MOTIVAZIONE = "motivazione";

    /**
     * {@inheritDoc}
     * Aggiunge un nuovo ricovero per un paziente nel database.
     *
     * @param cfPaziente  Il codice fiscale del paziente.
     * @param idLetto     L'identificativo del letto assegnato.
     * @param reparto     Il reparto in cui si trova il letto.
     * @param dataInizio  La data e l'ora di inizio del ricovero.
     * @param motivazione Il motivo del ricovero.
     */
    @Override
    public boolean aggiungiRicovero(String cfPaziente, String idLetto, String reparto, String dataInizio, String motivazione) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_RICOVERO_QUERY)) {
            stmt.setString(1, cfPaziente);
            stmt.setString(2, idLetto);
            stmt.setString(3, reparto);
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(dataInizio));
            stmt.setString(5, motivazione);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del ricovero", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * Recupera il ricovero attualmente attivo per un paziente specifico.
     *
     * @param cfPaziente Il codice fiscale del paziente.
     */
    @Override
    public ArrayList<String> getRicoveroAttivo(String cfPaziente) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_RICOVERO_ATTIVO_QUERY)) {
            stmt.setString(1, cfPaziente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return extractRicoveroAttivoFromResultSet(rs);
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del ricovero attivo", e);
        }
        return new ArrayList<>();
    }

    /**
     * Questo metodo non è implementato in questa classe. La logica di dimissione
     * è gestita da {@link DimissioniPostgresDAO#creaDimissione(String, String, String, String)}.
     *
     * @return sempre {@code false}.
     */
    @Override
    public boolean aggiornaRicoveroDimissione(String idRicovero, String dataFine, String prognosi, String esito) {
        return false;
    }

    /**
     * Questo metodo non è implementato. Utilizzare {@link DimissioniPostgresDAO#getUltimoRicoveroChiuso(String)}.
     * @return una lista vuota.
     */
    @Override
    public ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente) {
        return new ArrayList<>();
    }

    /**
     * Questo metodo non è implementato. Utilizzare {@link DimissioniPostgresDAO#getAllDimissioni()}.
     * @return una lista vuota.
     */
    @Override
    public ArrayList<ArrayList<String>> getAllDimissioni() {
        return new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<ArrayList<String>> getStoricoRicoveri(String cfPaziente) {
        ArrayList<ArrayList<String>> storico = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_STORICO_RICOVERI_QUERY)) {
            stmt.setString(1, cfPaziente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    storico.add(extractRicoveroCompletoFromResultSet(rs));
                }
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dello storico ricoveri", e);
        }
        return storico;
    }

    /** {@inheritDoc} */
    @Override
    public List<ArrayList<String>> getAllRicoveriAttivi() {
        List<ArrayList<String>> ricoveri = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_RICOVERI_ATTIVI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ricoveri.add(extractRicoveroAttivoFromResultSet(rs));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutti i ricoveri attivi", e);
        }
        return ricoveri;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLettoAttualmenteOccupato(String idLetto, String reparto) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(IS_LETTO_ATTUALMENTE_OCCUPATO_QUERY)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante la verifica dello stato di occupazione del letto " + idLetto);
            return true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ArrayList<String>> getStoricoRicoveriByLetto(String idLetto, String reparto) {
        List<ArrayList<String>> storico = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_STORICO_RICOVERI_BY_LETTO_QUERY)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    storico.add(extractRicoveroCompletoFromResultSet(rs));
                }
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante il recupero dello storico ricoveri per il letto " + idLetto + " nel reparto " + reparto);
        }
        return storico;
    }

    /** {@inheritDoc} */
    @Override
    public boolean aggiornaLettoRicovero(String idRicovero, String nuovoLetto, String nuovoReparto) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_LETTO_RICOVERO_QUERY)) {
            stmt.setString(1, nuovoLetto);
            stmt.setString(2, nuovoReparto);
            stmt.setInt(3, Integer.parseInt(idRicovero));
            int righeModificate = stmt.executeUpdate();
            return righeModificate > 0;
        } catch (SQLException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento del letto del ricovero", e);
        }
        return false;
    }

    /**
     * Metodo helper per estrarre i dati di un ricovero attivo da un ResultSet.
     *
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe contenente i dati del ricovero.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati.
     */
    private ArrayList<String> extractRicoveroAttivoFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> ricovero = new ArrayList<>();
        ricovero.add(String.valueOf(rs.getInt(COL_ID_RICOVERO)));
        ricovero.add(rs.getString(COL_CF));
        ricovero.add(rs.getString(COL_ID_LETTO));
        ricovero.add(rs.getString(COL_REPARTO));
        java.sql.Timestamp dataInizio = rs.getTimestamp(COL_DATA_INIZIO);
        ricovero.add(dataInizio != null ? dataInizio.toString() : "");
        ricovero.add(rs.getString(COL_MOTIVAZIONE));
        return ricovero;
    }

    /**
     * Metodo helper per estrarre i dati di un ricovero completo (anche chiuso) da un ResultSet.
     *
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe contenente i dati completi del ricovero.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati.
     */
    private ArrayList<String> extractRicoveroCompletoFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> ricovero = extractRicoveroAttivoFromResultSet(rs);
        ricovero.remove(ricovero.size() - 1);
        java.sql.Timestamp dataFine = rs.getTimestamp("data_fine");
        ricovero.add(dataFine != null ? dataFine.toString() : "In corso");
        ricovero.add(rs.getString(COL_MOTIVAZIONE));
        ricovero.add(rs.getString("prognosi"));
        ricovero.add(rs.getString("esito"));
        return ricovero;
    }
}