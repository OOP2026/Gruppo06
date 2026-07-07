package implementazioneDao;

import dao.LettoDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LettoPostgresDao implements LettoDAO {

    private static final Logger LOGGER = Logger.getLogger(LettoPostgresDao.class.getName());

    @Override
    public boolean aggiungiLetto(String idLetto, String reparto) {
        // Questa funzione non è più del tutto compatibile con la nuova struttura del DB (manca num_stanza)
        // e andrebbe aggiornata o rimossa. Per ora, la lasciamo parzialmente funzionante.
        String query = "INSERT INTO letto (numero_letto, reparto_di_appartenenza, is_libero, num_stanza) VALUES (?, ?, true, 'NON SPECIFICATA')";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            return stmt.executeUpdate() > 0; // Restituisce true se l'inserimento va a buon fine
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del letto", e);
        }
        return false;
    }

    // NOTA: Aggiorna la firma in LettoDAO aggiungendo 'String reparto'
    @Override
    public ArrayList<String> getLettoById(String idLetto, String reparto) {
        String query = "SELECT numero_letto, reparto_di_appartenenza, is_libero, num_stanza FROM letto WHERE numero_letto = ? AND reparto_di_appartenenza = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> letto = new ArrayList<>();
                boolean isLibero = rs.getBoolean("is_libero");
                letto.add(rs.getString("numero_letto"));
                letto.add(rs.getString("reparto_di_appartenenza"));
                letto.add(String.valueOf(!isLibero)); // Convertiamo is_libero in 'occupato' per il resto dell'app
                letto.add(rs.getString("num_stanza"));
                return letto;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del letto per ID", e);
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<ArrayList<String>> getAllLetti() {
        ArrayList<ArrayList<String>> letti = new ArrayList<>();
        String query = "SELECT numero_letto, reparto_di_appartenenza, is_libero, num_stanza FROM letto ORDER BY reparto_di_appartenenza, num_stanza, numero_letto";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ArrayList<String> letto = new ArrayList<>();
                boolean isLibero = rs.getBoolean("is_libero");
                letto.add(rs.getString("numero_letto"));
                letto.add(rs.getString("reparto_di_appartenenza"));
                letto.add(String.valueOf(!isLibero)); // Convertiamo is_libero in 'occupato'
                letto.add(rs.getString("num_stanza"));
                letti.add(letto);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutti i letti", e);
        }
        return letti;
    }

    // NOTA: Aggiorna la firma in LettoDAO aggiungendo 'String reparto'
    @Override
    public boolean aggiornaStatoLetto(String idLetto, String reparto, boolean occupato) {
        String query = "UPDATE letto SET is_libero = ? WHERE numero_letto = ? AND reparto_di_appartenenza = ?";
        boolean isLibero = !occupato; // La logica è invertita: se è occupato, non è libero.
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isLibero);
            stmt.setString(2, idLetto);
            stmt.setString(3, reparto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dello stato del letto", e);
        }
        return false;
    }
}