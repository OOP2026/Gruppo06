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

public class PrestazionePostgresDao implements PrestazioneDAO {

    private static final Logger LOGGER = Logger.getLogger(PrestazionePostgresDao.class.getName());

    private static final String AGGIUNGI_PRESTAZIONE_QUERY = "INSERT INTO prestazione (tipologia_prestazione, esito_prestazione, id_turno, cf_paziente, matricola_medico, id_agenda) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_ALL_PRESTAZIONI_QUERY = "SELECT p.id_prestazione, p.tipologia_prestazione, p.esito_prestazione, t.data_turno  AS data_turno, p.cf_paziente, p.matricola_medico FROM prestazione p LEFT JOIN turno_lavorativo t ON p.id_turno = t.id_turno ORDER BY p.id_prestazione ASC";
    private static final String GET_PRESTAZIONI_BY_MEDICO_QUERY = "SELECT p.id_prestazione, p.tipologia_prestazione, p.esito_prestazione, t.data_turno  AS data_turno, p.cf_paziente, p.matricola_medico FROM prestazione p LEFT JOIN turno_lavorativo t ON p.id_turno = t.id_turno WHERE p.matricola_medico = ? ORDER BY p.id_prestazione ASC";

    @Override
    public boolean aggiungiPrestazione(String tipologiaPrestazione, String esitoPrestazione, String idTurno, String cfPaziente, String matricolaMedico, String idAgenda) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_PRESTAZIONE_QUERY)) {
            stmt.setString(1, tipologiaPrestazione);
            stmt.setString(2, esitoPrestazione);
            stmt.setInt(3, Integer.parseInt(idTurno));
            stmt.setString(4, cfPaziente);
            stmt.setString(5, matricolaMedico);
            stmt.setInt(6, Integer.parseInt(idAgenda));
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta della prestazione", e);
            return false;
        }
    }

    @Override
    public ArrayList<ArrayList<String>> getAllPrestazioni() {
        ArrayList<ArrayList<String>> prestazioni = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_PRESTAZIONI_QUERY);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ArrayList<String> p = new ArrayList<>();
                p.add(String.valueOf(rs.getInt("id_prestazione")));
                p.add(rs.getString("tipologia_prestazione"));
                p.add(rs.getString("esito_prestazione"));
                p.add(rs.getString("data_turno"));
                p.add(rs.getString("cf_paziente"));
                p.add(rs.getString("matricola_medico"));
                prestazioni.add(p);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle prestazioni", e);
        }
        return prestazioni;
    }

    @Override
    public ArrayList<ArrayList<String>> getPrestazioniByMedico(String matricola) {
        ArrayList<ArrayList<String>> prestazioni = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_PRESTAZIONI_BY_MEDICO_QUERY)) {
            stmt.setString(1, matricola);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> p = new ArrayList<>();
                    p.add(String.valueOf(rs.getInt("id_prestazione")));
                    p.add(rs.getString("tipologia_prestazione"));
                    p.add(rs.getString("esito_prestazione"));
                    p.add(rs.getString("data_turno"));
                    p.add(rs.getString("cf_paziente"));
                    p.add(rs.getString("matricola_medico"));
                    prestazioni.add(p);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle prestazioni per il medico " + matricola, e);
        }
        return prestazioni;
    }
}