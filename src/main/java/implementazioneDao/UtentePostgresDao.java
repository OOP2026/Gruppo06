package implementazioneDao;

import dao.UtenteDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UtentePostgresDao implements UtenteDAO{

        @Override
        public boolean checkLoginEsistente(String login) {
            String query = "SELECT 1 FROM utente WHERE login = ?";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, login);
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // Ritorna true se trova una corrispondenza

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean aggiungiUtente(String nome, String cognome, String login, String password, String matricola, String pin) {
            String query = "INSERT INTO utente (login, password, matricola, nome, cognome, ruolo, pin) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, login);
                stmt.setString(2, password);
                stmt.setString(3, matricola);
                stmt.setString(4, nome);
                stmt.setString(5, cognome);

                if (pin != null && !pin.trim().isEmpty()) {
                    stmt.setString(6, "ADMIN");
                    stmt.setString(7, pin);
                } else {
                    stmt.setString(6, "MEDICO");
                    stmt.setNull(7, java.sql.Types.VARCHAR);
                }

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public ArrayList<String> getUtenteByLoginAndPassword(String login, String password) {
            String query = "SELECT * FROM utente WHERE login = ? AND password = ?";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, login);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    ArrayList<String> datiUtente = new ArrayList<>();
                    datiUtente.add(rs.getString("nome"));
                    datiUtente.add(rs.getString("cognome"));
                    datiUtente.add(rs.getString("login"));
                    datiUtente.add(rs.getString("password"));
                    datiUtente.add(rs.getString("matricola"));
                    datiUtente.add(rs.getString("ruolo"));
                    
                    String pinEstratto = rs.getString("pin");
                    datiUtente.add(pinEstratto != null ? pinEstratto : "");
                    
                    return datiUtente;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
}
