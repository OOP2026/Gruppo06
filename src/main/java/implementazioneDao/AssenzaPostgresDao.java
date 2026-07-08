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

public class AssenzaPostgresDao implements AssenzaDAO {

    private static final Logger LOGGER = Logger.getLogger(AssenzaPostgresDao.class.getName());
    
    private static final String AGGIUNGI_ASSENZA_QUERY = "INSERT INTO assenze (matricola, data_inizio, data_fine, motivazione, approvazione) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_ASSENZA_QUERY = "SELECT * FROM assenze WHERE matricola = ? AND data_inizio = ?";
    private static final String GET_ASSENZE_BY_MEDICO_QUERY = "SELECT * FROM assenze WHERE matricola = ? ORDER BY data_inizio ASC";
    private static final String AGGIORNA_ASSENZA_QUERY = "UPDATE assenze SET data_fine = ?, motivazione = ?, approvazione = ? WHERE matricola = ? AND data_inizio = ?";
    private static final String ELIMINA_ASSENZA_QUERY = "DELETE FROM assenze WHERE matricola = ? AND data_inizio = ?";

    // Costanti per i nomi delle colonne
    private static final String COL_MATRICOLA = "matricola";
    private static final String COL_DATA_INIZIO = "data_inizio";
    private static final String COL_DATA_FINE = "data_fine";
    private static final String COL_MOTIVAZIONE = "motivazione";
    private static final String COL_APPROVAZIONE = "approvazione";

    @Override
    public boolean aggiungiAssenza(String matricola, String dataInizio, String dataFine, String motivazione, boolean approvazione) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_ASSENZA_QUERY)) {
             
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(dataInizio));
            stmt.setDate(3, java.sql.Date.valueOf(dataFine));
            stmt.setString(4, motivazione);
            stmt.setBoolean(5, approvazione);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta dell'assenza", e);
        }
        return false;
    }

    @Override
    public ArrayList<String> getAssenza(String matricola, String dataInizio) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ASSENZA_QUERY)) {
             
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(dataInizio));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> assenza = new ArrayList<>();
                assenza.add(rs.getString(COL_MATRICOLA));

                java.sql.Date dataInizioDb = rs.getDate(COL_DATA_INIZIO);
                assenza.add(dataInizioDb != null ? dataInizioDb.toString() : "");

                java.sql.Date dataFineDb = rs.getDate(COL_DATA_FINE);
                assenza.add(dataFineDb != null ? dataFineDb.toString() : "");

                assenza.add(rs.getString(COL_MOTIVAZIONE));
                assenza.add(String.valueOf(rs.getBoolean(COL_APPROVAZIONE)));

                return assenza;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dell'assenza", e);
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<ArrayList<String>> getAssenzeByMedico(String matricola) {
        ArrayList<ArrayList<String>> assenze = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ASSENZE_BY_MEDICO_QUERY)) {
             
            stmt.setString(1, matricola);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ArrayList<String> assenza = new ArrayList<>();
                assenza.add(rs.getString(COL_MATRICOLA));

                java.sql.Date dataInizioDb = rs.getDate(COL_DATA_INIZIO);
                assenza.add(dataInizioDb != null ? dataInizioDb.toString() : "");

                java.sql.Date dataFineDb = rs.getDate(COL_DATA_FINE);
                assenza.add(dataFineDb != null ? dataFineDb.toString() : "");

                assenza.add(rs.getString(COL_MOTIVAZIONE));
                assenza.add(String.valueOf(rs.getBoolean(COL_APPROVAZIONE)));

                assenze.add(assenza);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero delle assenze del medico", e);
        }
        return assenze;
    }

    @Override
    public boolean aggiornaAssenza(String matricola, String dataInizio, String dataFine, String motivazione, boolean approvazione) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_ASSENZA_QUERY)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataFine));
            stmt.setString(2, motivazione);
            stmt.setBoolean(3, approvazione);
            stmt.setString(4, matricola);
            stmt.setDate(5, java.sql.Date.valueOf(dataInizio));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'assenza", e);
        }
        return false;
    }

    @Override
    public boolean eliminaAssenza(String matricola, String dataInizio) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ELIMINA_ASSENZA_QUERY)) {
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(dataInizio));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione dell'assenza", e);
        }
        return false;
    }
}