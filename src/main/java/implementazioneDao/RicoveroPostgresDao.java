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

public class RicoveroPostgresDao implements RicoveroDAO {

    private static final Logger LOGGER = Logger.getLogger(RicoveroPostgresDao.class.getName());

    // NOTA: Aggiorna la firma in RicoveroDAO aggiungendo 'String reparto'
    @Override
    public boolean aggiungiRicovero(String cfPaziente, String idLetto, String reparto, String dataInizio, String motivazione) {
        String query = "INSERT INTO ricovero (cf, id_letto, reparto, data_inizio, motivazione) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            stmt.setString(2, idLetto);
            stmt.setString(3, reparto);
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(dataInizio));
            stmt.setString(5, motivazione);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
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
        } catch (SQLException | IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento della dimissione", e);
        }
        return false;
    }

    @Override
    public ArrayList<String> getRicoveroAttivo(String cfPaziente) {
        // La query viene resa più robusta: in caso di dati corrotti (ricoveri multipli attivi per un paziente),
        // seleziona solo il più recente. Questo assicura che operazioni come la dimissione
        // agiscano sempre sull'ultimo ricovero valido.
        String query = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, motivazione FROM ricovero WHERE cf = ? AND data_fine IS NULL ORDER BY data_inizio DESC LIMIT 1";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> ricovero = new ArrayList<>();
                ricovero.add(String.valueOf(rs.getInt("id_ricovero")));
                ricovero.add(rs.getString("cf"));
                ricovero.add(rs.getString("id_letto"));
                ricovero.add(rs.getString("reparto"));
                java.sql.Timestamp dataInizio = rs.getTimestamp("data_inizio");
                ricovero.add(dataInizio != null ? dataInizio.toString() : "");
                ricovero.add(rs.getString("motivazione"));
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
        String query = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE data_fine IS NOT NULL ORDER BY data_fine DESC";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                ArrayList<String> ricovero = new ArrayList<>();
                ricovero.add(String.valueOf(rs.getInt("id_ricovero")));
                ricovero.add(rs.getString("cf"));
                ricovero.add(rs.getString("id_letto"));
                ricovero.add(rs.getString("reparto"));
                
                java.sql.Timestamp dataInizio = rs.getTimestamp("data_inizio");
                ricovero.add(dataInizio != null ? dataInizio.toString() : "");
                
                java.sql.Timestamp dataFine = rs.getTimestamp("data_fine");
                ricovero.add(dataFine != null ? dataFine.toString() : "");
                
                ricovero.add(rs.getString("motivazione"));
                ricovero.add(rs.getString("prognosi"));
                ricovero.add(rs.getString("esito"));
                
                dimissioni.add(ricovero);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutte le dimissioni", e);
        }
        return dimissioni;
    }

    @Override
    public ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente) {
        String query = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE cf = ? AND data_fine IS NOT NULL ORDER BY data_fine DESC LIMIT 1";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> ricovero = new ArrayList<>();
                ricovero.add(String.valueOf(rs.getInt("id_ricovero")));
                ricovero.add(rs.getString("cf"));
                ricovero.add(rs.getString("id_letto"));
                ricovero.add(rs.getString("reparto"));

                java.sql.Timestamp dataInizio = rs.getTimestamp("data_inizio");
                ricovero.add(dataInizio != null ? dataInizio.toString() : "");

                java.sql.Timestamp dataFine = rs.getTimestamp("data_fine");
                ricovero.add(dataFine != null ? dataFine.toString() : "");

                ricovero.add(rs.getString("motivazione"));
                ricovero.add(rs.getString("prognosi"));
                ricovero.add(rs.getString("esito"));
                return ricovero;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dell'ultimo ricovero chiuso per il paziente " + cfPaziente, e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<ArrayList<String>> getAllRicoveriAttivi() {
        List<ArrayList<String>> ricoveri = new ArrayList<>();
        // La query seleziona tutti i ricoveri che non hanno una data di fine
        String query = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, motivazione FROM ricovero WHERE data_fine IS NULL";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ArrayList<String> ricovero = new ArrayList<>();
                ricovero.add(String.valueOf(rs.getInt("id_ricovero")));
                ricovero.add(rs.getString("cf"));
                ricovero.add(rs.getString("id_letto"));
                ricovero.add(rs.getString("reparto"));
                java.sql.Timestamp dataInizio = rs.getTimestamp("data_inizio");
                ricovero.add(dataInizio != null ? dataInizio.toString() : "");
                ricovero.add(rs.getString("motivazione"));
                ricoveri.add(ricovero);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutti i ricoveri attivi", e);
        }
        return ricoveri;
    }

    // NOTA: Aggiorna la firma in RicoveroDAO aggiungendo 'String reparto'
    @Override
    public boolean isLettoAttualmenteOccupato(String idLetto, String reparto) {
        String query = "SELECT 1 FROM ricovero WHERE id_letto = ? AND reparto = ? AND data_fine IS NULL LIMIT 1";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Se rs.next() è true, esiste un record, quindi il letto è occupato.
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la verifica dello stato di occupazione del letto " + idLetto, e);
            // Failsafe: se non posso controllare, assumo sia occupato per prevenire doppie assegnazioni.
            return true;
        }
    }
}