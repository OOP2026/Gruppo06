package implementazioneDao;

import dao.PazienteDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PazientePostgresDao implements PazienteDAO {
    @Override
    public boolean aggiungiPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi) {
        String query = "INSERT INTO paziente (nome, cognome, cf, data_nascita, sesso, residenza, diagnosi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, cf);
            stmt.setDate(4, java.sql.Date.valueOf(dataNascita));
            stmt.setString(5, sesso);
            stmt.setString(6, residenza);
            stmt.setString(7, diagnosi);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<String> getPazienteByCf(String cf) {
        String query = "SELECT * FROM paziente WHERE cf = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, cf);
            ResultSet rs = stmt.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<ArrayList<String>> getAllPazienti() {
        ArrayList<ArrayList<String>> pazienti = new ArrayList<>();
        String query = "SELECT * FROM paziente";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
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
            e.printStackTrace();
        }
        return pazienti;
    }

    @Override
    public boolean aggiornaPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi) {
        String query = "UPDATE paziente SET nome = ?, cognome = ?, data_nascita = ?, sesso = ?, residenza = ?, diagnosi = ? WHERE cf = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setDate(3, java.sql.Date.valueOf(dataNascita));
            stmt.setString(4, sesso);
            stmt.setString(5, residenza);
            stmt.setString(6, diagnosi);
            stmt.setString(7, cf);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminaPaziente(String cf) {
        String query = "DELETE FROM paziente WHERE cf = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cf);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}