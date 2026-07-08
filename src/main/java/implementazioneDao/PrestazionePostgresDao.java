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

    private static final String AGGIUNGI_PRESTAZIONE_QUERY = "INSERT INTO prestazione (id_prestazione, tipologia_prestazione, esito_prestazione, id_turno, cf_paziente, matricola_medico, id_agenda) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_ALL_PRESTAZIONI_QUERY = "SELECT id_prestazione, tipologia_prestazione, esito_prestazione, id_turno, cf_paziente, matricola_medico, id_agenda FROM prestazione ORDER BY id_prestazione ASC";

    @Override
    public boolean aggiungiPrestazione(int idPrestazione, String tipologiaPrestazione, String esitoPrestazione, String idTurno, String cfPaziente, String matricolaMedico, String idAgenda) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_PRESTAZIONE_QUERY)) {
            stmt.setInt(1, idPrestazione);
            stmt.setString(2, tipologiaPrestazione);
            stmt.setString(3, esitoPrestazione);
            stmt.setInt(4, Integer.parseInt(idTurno));
            stmt.setString(5, cfPaziente);
            stmt.setString(6, matricolaMedico);
            stmt.setInt(7, Integer.parseInt(idAgenda));
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
                p.add(rs.getString("id_turno"));
                p.add(rs.getString("cf_paziente"));
                p.add(rs.getString("matricola_medico"));
                p.add(rs.getString("id_agenda"));
                prestazioni.add(p);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle prestazioni", e);
        }
        return prestazioni;
    }
}