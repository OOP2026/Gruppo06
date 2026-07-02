package implementazioneDao;

import dao.LettoDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LettoPostgresDao implements LettoDAO {
    @Override
    public boolean aggiungiLetto(String idLetto, String reparto) {
        String query = "INSERT INTO letti (id_letto, reparto, occupato) VALUES (?, ?, false)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idLetto);
            stmt.setString(2, reparto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<String> getLettoById(String idLetto) {
        String query = "SELECT * FROM letti WHERE id_letto = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idLetto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> letto = new ArrayList<>();
                letto.add(rs.getString("id_letto"));
                letto.add(rs.getString("reparto"));
                letto.add(String.valueOf(rs.getBoolean("occupato")));
                return letto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<ArrayList<String>> getAllLetti() {
        return new ArrayList<>(); // Da implementare secondo necessità
    }

    @Override
    public boolean aggiornaStatoLetto(String idLetto, boolean occupato) {
        String query = "UPDATE letti SET occupato = ? WHERE id_letto = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, occupato);
            stmt.setString(2, idLetto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}