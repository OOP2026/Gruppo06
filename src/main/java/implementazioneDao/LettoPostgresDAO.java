package implementazioneDao;

import dao.LettoDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LettoPostgresDAO implements LettoDAO {

    private static final Logger LOGGER = Logger.getLogger(LettoPostgresDAO.class.getName());
    
    private static final String AGGIUNGI_LETTO_QUERY = "INSERT INTO letto (numero_letto, reparto_di_appartenenza, is_libero, num_stanza) VALUES (?, ?, true, 'NON SPECIFICATA')";
    private static final String GET_LETTO_BY_ID_QUERY = "SELECT numero_letto, reparto_di_appartenenza, is_libero, num_stanza FROM letto WHERE numero_letto = ? AND reparto_di_appartenenza = ?";
    private static final String GET_ALL_LETTI_QUERY = "SELECT numero_letto, reparto_di_appartenenza, is_libero, num_stanza FROM letto ORDER BY reparto_di_appartenenza, num_stanza, numero_letto";
    private static final String AGGIORNA_STATO_LETTO_QUERY = "UPDATE letto SET is_libero = ? WHERE numero_letto = ? AND reparto_di_appartenenza = ?";
    private static final String GET_ALL_REPARTI_QUERY = "SELECT DISTINCT reparto_di_appartenenza FROM letto WHERE reparto_di_appartenenza IS NOT NULL AND reparto_di_appartenenza <> '' ORDER BY reparto_di_appartenenza ASC";
    private static final String COL_NUMERO_LETTO = "numero_letto";
    private static final String COL_REPARTO_APPARTENENZA = "reparto_di_appartenenza";
    private static final String COL_IS_LIBERO = "is_libero";
    private static final String COL_NUM_STANZA = "num_stanza";


    @Override
    public boolean aggiungiLetto(String idLetto, String reparto) {
        // Questa funzione non è più del tutto compatibile con la nuova struttura del DB (manca num_stanza)
        // e andrebbe aggiornata o rimossa. Per ora, la lasciamo parzialmente funzionante.
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_LETTO_QUERY)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            return stmt.executeUpdate() > 0; // Restituisce true se l'inserimento va a buon fine
        } catch (SQLException | NullPointerException e) { // Aggiunto NullPointerException
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del letto", e);
        }
        return false;
    }

    @Override
    public ArrayList<String> getLettoById(String idLetto, String reparto) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_LETTO_BY_ID_QUERY)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> letto = new ArrayList<>();
                boolean isLibero = rs.getBoolean(COL_IS_LIBERO);
                letto.add(rs.getString(COL_NUMERO_LETTO));
                letto.add(rs.getString(COL_REPARTO_APPARTENENZA));
                letto.add(String.valueOf(!isLibero)); // Convertiamo is_libero in 'occupato' per il resto dell'app
                letto.add(rs.getString(COL_NUM_STANZA));
                return letto;
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del letto per ID", e);
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<ArrayList<String>> getAllLetti() {
        ArrayList<ArrayList<String>> letti = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_LETTI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ArrayList<String> letto = new ArrayList<>();
                boolean isLibero = rs.getBoolean(COL_IS_LIBERO);
                letto.add(rs.getString(COL_NUMERO_LETTO));
                letto.add(rs.getString(COL_REPARTO_APPARTENENZA));
                letto.add(String.valueOf(!isLibero)); // Convertiamo is_libero in 'occupato'
                letto.add(rs.getString(COL_NUM_STANZA));
                letti.add(letto);
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutti i letti", e);
        }
        return letti;
    }

    // NOTA: Aggiorna la firma in LettoDAO aggiungendo 'String reparto'
    @Override
    public boolean aggiornaStatoLetto(String idLetto, String reparto, boolean occupato) {
        boolean isLibero = !occupato; // La logica è invertita: se è occupato, non è libero.
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_STATO_LETTO_QUERY)) {
            stmt.setBoolean(1, isLibero);
            stmt.setString(2, idLetto);
            stmt.setString(3, reparto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dello stato del letto", e);
        }
        return false;
    }

    @Override
    public List<String> getAllReparti() {
        List<String> reparti = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_REPARTI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reparti.add(rs.getString(COL_REPARTO_APPARTENENZA));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei reparti", e);
        }
        return reparti;
    }
}