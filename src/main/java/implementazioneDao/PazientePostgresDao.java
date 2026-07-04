package implementazioneDao;

import dao.PazienteDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PazientePostgresDao implements PazienteDAO {

    // Centralizzazione delle query SQL come costanti
    private static final String COLUMNS = "cf, nome, cognome, data_nascita, sesso, residenza, diagnosi";
    private static final String AGGIUNGI_PAZIENTE_QUERY = "INSERT INTO pazienti (nome, cognome, cf, data_nascita, sesso, residenza, diagnosi) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_PAZIENTE_BY_CF_QUERY = "SELECT " + COLUMNS + " FROM pazienti WHERE cf = ?";
    private static final String GET_ALL_PAZIENTI_QUERY = "SELECT " + COLUMNS + " FROM pazienti ORDER BY cognome, nome";
    private static final String AGGIORNA_PAZIENTE_QUERY = "UPDATE pazienti SET nome = ?, cognome = ?, data_nascita = ?, sesso = ?, residenza = ?, diagnosi = ? WHERE cf = ?";
    private static final String ELIMINA_PAZIENTE_QUERY = "DELETE FROM pazienti WHERE cf = ?";

    @Override
    public boolean aggiungiPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_PAZIENTE_QUERY)) {
             
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, cf);
            stmt.setDate(4, java.sql.Date.valueOf(dataNascita));
            stmt.setString(5, sesso);
            stmt.setString(6, residenza);
            stmt.setString(7, diagnosi);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiunta del paziente nel database", e);
        }
    }

    @Override
    public ArrayList<String> getPazienteByCf(String cf) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_PAZIENTE_BY_CF_QUERY)) {
             
            stmt.setString(1, cf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ArrayList<String> paziente = new ArrayList<>();
                    paziente.add(rs.getString("cf"));
                    paziente.add(rs.getString("nome"));
                    paziente.add(rs.getString("cognome"));
                    java.sql.Date dataDb = rs.getDate("data_nascita");
                    paziente.add(dataDb != null ? dataDb.toString() : "");
                    paziente.add(rs.getString("sesso"));
                    paziente.add(rs.getString("residenza"));
                    paziente.add(rs.getString("diagnosi"));
                    return paziente;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del paziente dal database", e);
        }
        return null;
    }

    @Override
    public ArrayList<ArrayList<String>> getAllPazienti() {
        ArrayList<ArrayList<String>> pazienti = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_PAZIENTI_QUERY);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                ArrayList<String> datiPaziente = new ArrayList<>();
                datiPaziente.add(rs.getString("cf"));
                datiPaziente.add(rs.getString("nome"));
                datiPaziente.add(rs.getString("cognome"));
                java.sql.Date dataDb = rs.getDate("data_nascita");
                datiPaziente.add(dataDb != null ? dataDb.toString() : "");
                datiPaziente.add(rs.getString("sesso"));
                datiPaziente.add(rs.getString("residenza"));
                datiPaziente.add(rs.getString("diagnosi"));
                pazienti.add(datiPaziente);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero di tutti i pazienti dal database", e);
        }
        return pazienti;
    }

    @Override
    public boolean aggiornaPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_PAZIENTE_QUERY)) {

            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setDate(3, java.sql.Date.valueOf(dataNascita));
            stmt.setString(4, sesso);
            stmt.setString(5, residenza);
            stmt.setString(6, diagnosi);
            stmt.setString(7, cf);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento del paziente nel database", e);
        }
    }

    @Override
    public boolean eliminaPaziente(String cf) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ELIMINA_PAZIENTE_QUERY)) {
            stmt.setString(1, cf);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del paziente dal database", e);
        }
    }
}