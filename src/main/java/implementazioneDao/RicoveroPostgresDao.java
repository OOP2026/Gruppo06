package implementazioneDao;

import dao.RicoveroDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RicoveroPostgresDao implements RicoveroDAO {

    private static final Logger LOGGER = Logger.getLogger(RicoveroPostgresDao.class.getName());

    @Override
    public boolean aggiungiRicovero(String cfPaziente, String idLetto, String dataInizio, String motivo) {
        String query = "INSERT INTO ricovero (cf_paziente, id_letto, data_inizio, motivo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            stmt.setString(2, idLetto);
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(dataInizio));
            stmt.setString(4, motivo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del ricovero", e);
        }
        return false;
    }

    @Override
    public boolean aggiornaRicoveroDimissione(String idRicovero, String dataFine, String prognosi, String esito) {
        String query = "UPDATE ricovero SET data_fine = ?, prognosi = ?, esito = ? WHERE id_ricovero = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(dataFine));
            stmt.setString(2, prognosi);
            stmt.setString(3, esito);
            stmt.setInt(4, Integer.parseInt(idRicovero));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento della dimissione", e);
        }
        return false;
    }

    @Override
    public ArrayList<String> getRicoveroAttivo(String cfPaziente) {
        String query = "SELECT * FROM ricovero WHERE cf_paziente = ? AND data_fine IS NULL";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> ricovero = new ArrayList<>();
                ricovero.add(String.valueOf(rs.getInt("id_ricovero")));
                ricovero.add(rs.getString("cf_paziente"));
                ricovero.add(rs.getString("id_letto"));
                java.sql.Timestamp dataInizio = rs.getTimestamp("data_inizio");
                ricovero.add(dataInizio != null ? dataInizio.toString() : "");
                ricovero.add(rs.getString("motivo"));
                return ricovero;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del ricovero attivo", e);
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<ArrayList<String>> getStoricoRicoveri(String cfPaziente) {
        return new ArrayList<>(); // Implementazione selettiva se richiesta per ulteriori feature
    }

    @Override
    public ArrayList<ArrayList<String>> getAllDimissioni() {
        ArrayList<ArrayList<String>> dimissioni = new ArrayList<>();
        String query = "SELECT * FROM ricovero WHERE data_fine IS NOT NULL ORDER BY data_fine DESC";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                ArrayList<String> ricovero = new ArrayList<>();
                ricovero.add(String.valueOf(rs.getInt("id_ricovero")));
                ricovero.add(rs.getString("cf_paziente"));
                ricovero.add(rs.getString("id_letto"));
                
                java.sql.Timestamp dataInizio = rs.getTimestamp("data_inizio");
                ricovero.add(dataInizio != null ? dataInizio.toString() : "");
                
                java.sql.Timestamp dataFine = rs.getTimestamp("data_fine");
                ricovero.add(dataFine != null ? dataFine.toString() : "");
                
                ricovero.add(rs.getString("motivo"));
                ricovero.add(rs.getString("prognosi"));
                ricovero.add(rs.getString("esito"));
                
                dimissioni.add(ricovero);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutte le dimissioni", e);
        }
        return dimissioni;
    }
}