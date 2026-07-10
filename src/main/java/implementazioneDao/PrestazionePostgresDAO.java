package implementazioneDao;

import dao.PrestazioneDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia PrestazioneDAO per la gestione delle prestazioni mediche
 * su un database PostgreSQL.
 */
public class PrestazionePostgresDAO implements PrestazioneDAO {

    private static final Logger LOGGER = Logger.getLogger(PrestazionePostgresDAO.class.getName());
    private static final String AGGIUNGI_PRESTAZIONE_QUERY = "INSERT INTO prestazione (tipologia_prestazione, esito_prestazione, id_turno, cf_paziente, matricola_medico, id_agenda) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_ALL_PRESTAZIONI_QUERY = "SELECT p.id_prestazione, p.tipologia_prestazione, p.esito_prestazione, t.data_turno AS data_turno, p.cf_paziente, p.matricola_medico, p.referto FROM prestazione p LEFT JOIN turno_lavorativo t ON p.id_turno = t.id_turno ORDER BY p.id_prestazione ASC";
    private static final String GET_PRESTAZIONI_BY_MEDICO_QUERY = "SELECT p.id_prestazione, p.tipologia_prestazione, p.esito_prestazione, t.data_turno AS data_turno, p.cf_paziente, p.matricola_medico, p.referto FROM prestazione p LEFT JOIN turno_lavorativo t ON p.id_turno = t.id_turno WHERE p.matricola_medico = ? ORDER BY p.id_prestazione ASC";
    private static final String GET_PRESTAZIONE_BY_ID_QUERY = "SELECT p.id_prestazione, p.tipologia_prestazione, p.esito_prestazione, t.data_turno AS data_turno, p.cf_paziente, p.matricola_medico, p.referto FROM prestazione p LEFT JOIN turno_lavorativo t ON p.id_turno = t.id_turno WHERE p.id_prestazione = ?";
    private static final String UPDATE_PRESTAZIONE_QUERY = "UPDATE prestazione SET tipologia_prestazione = ?, esito_prestazione = ?, referto = ? WHERE id_prestazione = ?";
    private static final String DELETE_PRESTAZIONE_QUERY = "DELETE FROM prestazione WHERE id_prestazione = ?";

    private static final String COL_ID_PRESTAZIONE = "id_prestazione";
    private static final String COL_TIPOLOGIA_PRESTAZIONE = "tipologia_prestazione";
    private static final String COL_ESITO_PRESTAZIONE = "esito_prestazione";
    private static final String COL_DATA_TURNO = "data_turno";
    private static final String COL_CF_PAZIENTE = "cf_paziente";
    private static final String COL_MATRICOLA_MEDICO = "matricola_medico";
    private static final String COL_REFERTO = "referto";

    /**
     * {@inheritDoc}
     * Aggiunge una nuova prestazione medica al database.
     * @param tipologiaPrestazione la descrizione della prestazione.
     * @param esitoPrestazione l'esito della prestazione (es. "Erogata", "Non erogata").
     * @param idTurno l'ID del turno lavorativo associato.
     * @param cfPaziente il codice fiscale del paziente.
     * @param matricolaMedico la matricola del medico che ha erogato la prestazione.
     * @param idAgenda l'ID dell'agenda a cui la prestazione è collegata.
     * @return {@code true} se l'aggiunta ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean aggiungiPrestazione(String tipologiaPrestazione, String esitoPrestazione, String idTurno, String cfPaziente, String matricolaMedico, String idAgenda) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_PRESTAZIONE_QUERY)) {
            stmt.setString(1, tipologiaPrestazione);
            stmt.setString(2, esitoPrestazione);
            stmt.setInt(3, Integer.parseInt(idTurno));
            stmt.setString(4, cfPaziente);
            stmt.setString(5, matricolaMedico);
            stmt.setInt(6, Integer.parseInt(idAgenda));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NumberFormatException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta della prestazione", e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * Recupera tutte le prestazioni mediche dal database.
     * @return una lista di tutte le prestazioni.
     */
    @Override
    public ArrayList<ArrayList<String>> getAllPrestazioni() {
        ArrayList<ArrayList<String>> prestazioni = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_PRESTAZIONI_QUERY);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prestazioni.add(extractPrestazioneFromResultSet(rs));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle prestazioni", e);
        }
        return prestazioni;
    }

    /**
     * {@inheritDoc}
     * Recupera tutte le prestazioni erogate da un medico specifico.
     * @param matricola la matricola del medico.
     * @return una lista delle prestazioni associate al medico.
     */
    @Override
    public ArrayList<ArrayList<String>> getPrestazioniByMedico(String matricola) {
        ArrayList<ArrayList<String>> prestazioni = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_PRESTAZIONI_BY_MEDICO_QUERY)) {
            stmt.setString(1, matricola);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestazioni.add(extractPrestazioneFromResultSet(rs));
                }
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante il recupero delle prestazioni per il medico " + matricola);
        }
        return prestazioni;
    }

    /**
     * {@inheritDoc}
     * Recupera una singola prestazione tramite il suo ID.
     * @param idPrestazione l'ID della prestazione da cercare.
     * @return un'ArrayList con i dati della prestazione, o {@code null} se non trovata.
     */
    @Override
    public ArrayList<String> getPrestazioneById(String idPrestazione) {
        ArrayList<String> prestazione = null;
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_PRESTAZIONE_BY_ID_QUERY)) {
            stmt.setInt(1, Integer.parseInt(idPrestazione));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prestazione = extractPrestazioneFromResultSet(rs);
                }
            }
        } catch (SQLException | NumberFormatException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante il recupero della prestazione con ID " + idPrestazione);
        }
        return prestazione;
    }

    /**
     * {@inheritDoc}
     * Aggiorna i dettagli di una prestazione esistente.
     * @param idPrestazione l'ID della prestazione da aggiornare.
     * @param tipologia la nuova tipologia della prestazione.
     * @param esito il nuovo esito della prestazione.
     * @param referto il nuovo testo del referto.
     * @return {@code true} se l'aggiornamento ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean updatePrestazione(int idPrestazione, String tipologia, String esito, String referto) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PRESTAZIONE_QUERY)) {
            stmt.setString(1, tipologia);
            stmt.setString(2, esito);
            stmt.setString(3, referto);
            stmt.setInt(4, idPrestazione);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante l'aggiornamento della prestazione con ID " + idPrestazione);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * Elimina una prestazione dal database.
     * @param idPrestazione l'ID della prestazione da eliminare.
     * @return {@code true} se l'eliminazione ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean eliminaPrestazione(int idPrestazione) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(DELETE_PRESTAZIONE_QUERY)) {
            stmt.setInt(1, idPrestazione);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante l'eliminazione della prestazione con ID " + idPrestazione);
            return false;
        }
    }

    /**
     * Metodo helper per estrarre i dati di una prestazione da un {@link ResultSet}.
     *
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe contenente i dati della prestazione.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati.
     */
    private ArrayList<String> extractPrestazioneFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> p = new ArrayList<>();
        p.add(String.valueOf(rs.getInt(COL_ID_PRESTAZIONE)));
        p.add(rs.getString(COL_TIPOLOGIA_PRESTAZIONE));
        p.add(rs.getString(COL_ESITO_PRESTAZIONE));
        p.add(rs.getString(COL_DATA_TURNO));
        p.add(rs.getString(COL_CF_PAZIENTE));
        p.add(rs.getString(COL_MATRICOLA_MEDICO));
        p.add(rs.getString(COL_REFERTO));
        return p;
    }
}