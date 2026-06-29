package implementazioneDao;

import dao.UtenteDAO;
import database_connection.ConnessioneDatabase;
import model.Amministratore;
import model.Medico;
import model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtentePostgresDao implements UtenteDAO{

        @Override
        public boolean checkLoginEsistente(String login) {
            String query = "SELECT 1 FROM utenti WHERE login = ?";
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
        public boolean aggiungiUtente(Utente utente, boolean isAdmin, String pin) {
            String query = "INSERT INTO utenti (login, password, matricola, nome, cognome, ruolo, pin) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, utente.getLogin());
                stmt.setString(2, utente.getPassword());
                stmt.setString(3, utente.getMatricola());

                if (isAdmin) {
                    Amministratore admin = (Amministratore) utente;
                    stmt.setString(4, admin.getNome());
                    stmt.setString(5, admin.getCognome());

                    stmt.setString(6, "ADMIN");
                    stmt.setString(7, pin); //salva il pin per l'admin
                } else {
                    Medico medico = (Medico) utente;
                    stmt.setString(4, medico.getNome());
                    stmt.setString(5, medico.getCognome());

                    stmt.setString(6, "MEDICO");
                    stmt.setNull(7, java.sql.Types.VARCHAR); //setta a NULL se non è un admin
                }

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public Utente getUtenteByLoginAndPassword(String login, String password) {
            String query = "SELECT * FROM utenti WHERE login = ? AND password = ?";
            try (Connection conn = ConnessioneDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, login);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String ruolo = rs.getString("ruolo");
                    if ("ADMIN".equals(ruolo)) {
                        return new Amministratore(rs.getString("login"), rs.getString("password"), rs.getString("matricola"), rs.getString("nome"), rs.getString("cognome"), rs.getString("pin"));
                    } else if ("MEDICO".equals(ruolo)) {
                        return new Medico(rs.getString("nome"), rs.getString("cognome"), rs.getString("login"), rs.getString("password"), rs.getString("matricola"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
}
