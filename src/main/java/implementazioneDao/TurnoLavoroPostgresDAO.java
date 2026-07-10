package implementazioneDao;

import dao.TurnoLavoroDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia TurnoLavoroDAO per la gestione dei turni di lavoro
 * su un database PostgreSQL.
 * Questa classe utilizza la nuova API java.time per la gestione di date e orari.
 */
public class TurnoLavoroPostgresDAO implements TurnoLavoroDAO {

    private static final Logger LOGGER = Logger.getLogger(TurnoLavoroPostgresDAO.class.getName());
    private static final String COL_ID_TURNO = "id_turno";
    private static final String COL_MATRICOLA_MEDICO = "matricola_medico";
    private static final String COL_DATA_TURNO = "data_turno";
    private static final String COL_ORA_INIZIO = "ora_inizio";
    private static final String COL_ORA_FINE = "ora_fine";
    private static final String COLUMNS_TO_SELECT = COL_ID_TURNO + ", " + COL_MATRICOLA_MEDICO + ", " + COL_DATA_TURNO + ", " + COL_ORA_INIZIO + ", " + COL_ORA_FINE;
    private static final String AGGIUNGI_TURNO_QUERY = "INSERT INTO turno_lavorativo (data_turno, ora_inizio, ora_fine, matricola_medico, id_agenda) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_TURNO_QUERY = "SELECT " + COLUMNS_TO_SELECT + " FROM turno_lavorativo WHERE matricola_medico = ? AND data_turno = ? AND ora_inizio = ?";
    private static final String GET_TURNI_BY_MEDICO_QUERY = "SELECT " + COLUMNS_TO_SELECT + " FROM  turno_lavorativo WHERE matricola_medico = ? ORDER BY data_turno ASC, ora_inizio ASC";
    private static final String AGGIORNA_TURNO_QUERY = "UPDATE  turno_lavorativo SET ora_inizio = ?, ora_fine = ? WHERE matricola_medico = ? AND data_turno = ? AND ora_inizio = ?";
    private static final String ELIMINA_TURNO_QUERY = "DELETE FROM  turno_lavorativo WHERE matricola_medico = ? AND data_turno = ? AND ora_inizio = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno, String idAgenda) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_TURNO_QUERY)) {

            stmt.setObject(1, LocalDate.parse(data));
            stmt.setObject(2, LocalTime.parse(inizioTurno));
            stmt.setObject(3, LocalTime.parse(fineTurno));
            stmt.setString(4, matricola);
            stmt.setInt(5, Integer.parseInt(idAgenda));

            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del turno", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<String> getTurno(String matricola, String data, String inizioTurno) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_TURNO_QUERY)) {

            stmt.setString(1, matricola);
            stmt.setObject(2, LocalDate.parse(data));
            stmt.setObject(3, LocalTime.parse(inizioTurno));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractTurnoFromResultSet(rs);
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del turno", e);
        }
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ArrayList<String>> getTurniByMedico(String matricola) {
        ArrayList<ArrayList<String>> turni = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_TURNI_BY_MEDICO_QUERY)) {

            stmt.setString(1, matricola);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                turni.add(extractTurnoFromResultSet(rs));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei turni per medico", e);
        }
        return turni;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean aggiornaTurno(String matricola, String data, String vecchioInizio, String nuovoInizio, String nuovaFine) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_TURNO_QUERY)) {
            stmt.setObject(1, LocalTime.parse(nuovoInizio));
            stmt.setObject(2, LocalTime.parse(nuovaFine));
            stmt.setString(3, matricola);
            stmt.setObject(4, LocalDate.parse(data));
            stmt.setObject(5, LocalTime.parse(vecchioInizio));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento del turno", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eliminaTurno(String matricola, String data, String inizioTurno) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(ELIMINA_TURNO_QUERY)) {
            stmt.setString(1, matricola);
            stmt.setObject(2, LocalDate.parse(data));
            stmt.setObject(3, LocalTime.parse(inizioTurno));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione del turno", e);
        }
        return false; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean aggiornaMedicoTurno(int idTurno, String nuovaMatricola) {
        final String UPDATE_QUERY = "UPDATE turno_lavorativo SET matricola_medico = ? WHERE id_turno = ?";
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY)) {
            stmt.setString(1, nuovaMatricola);
            stmt.setInt(2, idTurno);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore durante l'aggiornamento del medico per il turno ID: " + idTurno);
        }
        return false;
    }

    /**
     * Metodo helper per estrarre i dati di un turno da un ResultSet.
     *
     * @param rs il ResultSet da cui estrarre i dati.
     * @return un'ArrayList di stringhe contenente i dati del turno.
     * @throws SQLException se si verifica un errore durante l'accesso ai dati.
     */
    private ArrayList<String> extractTurnoFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> turno = new ArrayList<>();
        turno.add(rs.getString(COL_ID_TURNO));
        turno.add(rs.getString(COL_MATRICOLA_MEDICO));

        LocalDate dataDb = rs.getObject(COL_DATA_TURNO, LocalDate.class);
        turno.add(dataDb != null ? dataDb.toString() : "");

        LocalTime inizioDb = rs.getObject(COL_ORA_INIZIO, LocalTime.class);
        turno.add(inizioDb != null ? inizioDb.toString() : "");

        LocalTime fineDb = rs.getObject(COL_ORA_FINE, LocalTime.class);
        turno.add(fineDb != null ? fineDb.toString() : "");
        return turno;
    }
}