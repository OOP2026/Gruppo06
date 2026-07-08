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
    
    private static final String AGGIUNGI_RICOVERO_QUERY = "INSERT INTO ricovero (cf, id_letto, reparto, data_inizio, motivazione) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_RICOVERO_ATTIVO_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, motivazione FROM ricovero WHERE cf = ? AND data_fine IS NULL ORDER BY data_inizio DESC LIMIT 1";
    private static final String GET_ALL_RICOVERI_ATTIVI_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, motivazione FROM ricovero WHERE data_fine IS NULL";
    private static final String IS_LETTO_ATTUALMENTE_OCCUPATO_QUERY = "SELECT 1 FROM ricovero WHERE id_letto = ? AND reparto = ? AND data_fine IS NULL LIMIT 1";
    private static final String GET_STORICO_RICOVERI_QUERY = "SELECT id_ricovero, cf, id_letto, reparto, data_inizio, data_fine, motivazione, prognosi, esito FROM ricovero WHERE cf = ? ORDER BY data_inizio DESC";

    // Costanti per i nomi delle colonne per evitare duplicazioni e code smells
    private static final String COL_ID_RICOVERO = "id_ricovero";
    private static final String COL_CF = "cf";
    private static final String COL_ID_LETTO = "id_letto";
    private static final String COL_REPARTO = "reparto";
    private static final String COL_DATA_INIZIO = "data_inizio";
    private static final String COL_MOTIVAZIONE = "motivazione";

    // NOTA: Aggiorna la firma in RicoveroDAO aggiungendo 'String reparto'
    @Override
    public boolean aggiungiRicovero(String cfPaziente, String idLetto, String reparto, String dataInizio, String motivazione) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_RICOVERO_QUERY)) {
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
    public ArrayList<String> getRicoveroAttivo(String cfPaziente) {
        // La query viene resa più robusta: in caso di dati corrotti (ricoveri multipli attivi per un paziente),
        // seleziona solo il più recente. Questo assicura che operazioni come la dimissione
        // agiscano sempre sull'ultimo ricovero valido.
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_RICOVERO_ATTIVO_QUERY)) {
            stmt.setString(1, cfPaziente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del ricovero attivo", e);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean aggiornaRicoveroDimissione(String idRicovero, String dataFine, String prognosi, String esito) {
        return false; // Logica spostata in DimissioniDAO
    }

    @Override
    public ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente) {
        return new ArrayList<>(); // Logica spostata in DimissioniDAO
    }

    @Override
    public ArrayList<ArrayList<String>> getAllDimissioni() {
        return new ArrayList<>(); // Logica spostata in DimissioniDAO
    }

    @Override
    public ArrayList<ArrayList<String>> getStoricoRicoveri(String cfPaziente) {
        ArrayList<ArrayList<String>> storico = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_STORICO_RICOVERI_QUERY)) {
            stmt.setString(1, cfPaziente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> ricovero = new ArrayList<>();
                    ricovero.add(String.valueOf(rs.getInt(COL_ID_RICOVERO)));
                    ricovero.add(rs.getString(COL_CF));
                    ricovero.add(rs.getString(COL_ID_LETTO));
                    ricovero.add(rs.getString(COL_REPARTO));
                    java.sql.Timestamp dataInizio = rs.getTimestamp(COL_DATA_INIZIO);
                    ricovero.add(dataInizio != null ? dataInizio.toString() : "");
                    java.sql.Timestamp dataFine = rs.getTimestamp("data_fine");
                    ricovero.add(dataFine != null ? dataFine.toString() : "In corso");
                    ricovero.add(rs.getString(COL_MOTIVAZIONE));
                    ricovero.add(rs.getString("prognosi"));
                    ricovero.add(rs.getString("esito"));
                    storico.add(ricovero);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dello storico ricoveri", e);
        }
        return storico;
    }

    @Override
    public List<ArrayList<String>> getAllRicoveriAttivi() {
        List<ArrayList<String>> ricoveri = new ArrayList<>();
        // La query seleziona tutti i ricoveri che non hanno una data di fine
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_RICOVERI_ATTIVI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ArrayList<String> ricovero = new ArrayList<>();
                ricovero.add(String.valueOf(rs.getInt(COL_ID_RICOVERO)));
                ricovero.add(rs.getString(COL_CF));
                ricovero.add(rs.getString(COL_ID_LETTO));
                ricovero.add(rs.getString(COL_REPARTO));
                java.sql.Timestamp dataInizio = rs.getTimestamp(COL_DATA_INIZIO);
                ricovero.add(dataInizio != null ? dataInizio.toString() : "");
                ricovero.add(rs.getString(COL_MOTIVAZIONE));
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
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(IS_LETTO_ATTUALMENTE_OCCUPATO_QUERY)) {
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