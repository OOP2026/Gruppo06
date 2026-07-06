package implementazioneDao;

import dao.MedicoDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MedicoPostgresDao implements MedicoDAO {

    private static final Logger LOGGER = Logger.getLogger(MedicoPostgresDao.class.getName());

    @Override
    public boolean aggiungiMedico(String nome, String cognome, String matricola, String iscrizioneAlbo, String specializzazione, String reparto) {
        String query = "INSERT INTO medico (nome, cognome, matricola, iscrizione_albo, specializzazione, reparto) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, matricola);
            stmt.setString(4, iscrizioneAlbo);
            stmt.setString(5, specializzazione);
            stmt.setString(6, reparto);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del medico", e);
            return false;
        }
    }


    @Override
    public ArrayList<String> getMedicoByMatricola(String matricola) {
        String query = "SELECT * FROM utente WHERE matricola = ? AND ruolo = 'MEDICO'";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ArrayList<String> datiMedico = new ArrayList<>();
                datiMedico.add(rs.getString("nome"));
                datiMedico.add(rs.getString("cognome"));
                datiMedico.add(rs.getString("login"));
                datiMedico.add(rs.getString("password"));
                datiMedico.add(rs.getString("matricola"));

                java.sql.Date dataIscrizione = rs.getDate("iscrizione_albo");
                datiMedico.add(dataIscrizione != null ? dataIscrizione.toString() : "");

                datiMedico.add(rs.getString("specializzazione"));
                datiMedico.add(rs.getString("reparto"));

                return datiMedico;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del medico per matricola", e);
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<ArrayList<String>> getAllMedici() {
        ArrayList<ArrayList<String>> medici = new ArrayList<>();
        String query = "SELECT * FROM utente WHERE ruolo = 'MEDICO'";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ArrayList<String> datiMedico = new ArrayList<>();
                datiMedico.add(rs.getString("nome"));
                datiMedico.add(rs.getString("cognome"));
                datiMedico.add(rs.getString("login"));
                datiMedico.add(rs.getString("password"));
                datiMedico.add(rs.getString("matricola"));

                java.sql.Date dataIscrizione = rs.getDate("iscrizione_albo");
                datiMedico.add(dataIscrizione != null ? dataIscrizione.toString() : "");

                datiMedico.add(rs.getString("specializzazione"));
                datiMedico.add(rs.getString("reparto"));

                medici.add(datiMedico);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutti i medici", e);
        }
        return medici;
    }

    @Override
    public boolean aggiornaMedico(String nome, String cognome, String login, String password, String matricola, String iscrizioneAlbo, String specializzazione, String reparto) {
        String query = "UPDATE utente SET nome = ?, cognome = ?, login = ?, password = ?, iscrizione_albo = ?, specializzazione = ?, reparto = ? WHERE matricola = ? AND ruolo = 'MEDICO'";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, login);
            stmt.setString(4, password);

            if (iscrizioneAlbo != null && !iscrizioneAlbo.trim().isEmpty()) {
                stmt.setDate(5, java.sql.Date.valueOf(iscrizioneAlbo));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }

            stmt.setString(6, specializzazione);
            stmt.setString(7, reparto);
            stmt.setString(8, matricola);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento del medico", e);
        }
        return false;
    }

    @Override
    public boolean eliminaMedico(String matricola) {
        // Tipicamente è meglio disattivare l'utente piuttosto che eliminarlo fisicamente
        return false;
    }
}