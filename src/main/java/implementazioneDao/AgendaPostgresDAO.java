package implementazioneDao;

import dao.AgendaDAO;
import database_connection.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgendaPostgresDAO implements AgendaDAO {

    private static final Logger LOGGER = Logger.getLogger(AgendaPostgresDAO.class.getName());

    // Centralizzazione delle query SQL come costanti per migliorare la leggibilità e la manutenibilità
    private static final String GET_EVENTI_BY_MATRICOLA_QUERY = "SELECT id_agenda, titolo, descrizione, matricola_medico, data_ora_inizio, data_ora_fine, matricola_amministratore FROM agenda WHERE matricola_medico = ? OR matricola_amministratore = ? ORDER BY data_ora_inizio DESC";
    private static final String ADD_EVENTO_ADMIN_QUERY = "INSERT INTO agenda (titolo, descrizione, data_ora_inizio, data_ora_fine, matricola_amministratore) VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_EVENTO_MEDICO_QUERY = "INSERT INTO agenda (titolo, descrizione, data_ora_inizio, data_ora_fine, matricola_medico) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_EVENTO_QUERY = "UPDATE agenda SET titolo = ?, descrizione = ?, data_ora_inizio = ?, data_ora_fine = ? WHERE id_agenda = ?";
    private static final String DELETE_EVENTO_QUERY = "DELETE FROM agenda WHERE id_agenda = ?";
    private static final String CREA_AGENDA_QUERY = "INSERT INTO agenda (matricola_medico, titolo, descrizione, data_ora_inizio, data_ora_fine) VALUES (?, 'Agenda Principale', 'Agenda creata automaticamente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
    private static final String CREA_AGENDA_ADMIN_QUERY = "INSERT INTO agenda (matricola_amministratore, titolo, descrizione, data_ora_inizio, data_ora_fine) VALUES (?, 'Agenda Principale', 'Agenda creata automaticamente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    // Costanti per i nomi delle colonne
    private static final String COL_ID_AGENDA = "id_agenda";
    private static final String COL_MATRICOLA_MEDICO = "matricola_medico";
    private static final String COL_MATRICOLA_AMMINISTRATORE = "matricola_amministratore";
    private static final String COL_TITOLO = "titolo";
    private static final String COL_DESCRIZIONE = "descrizione";
    private static final String COL_DATA_ORA_INIZIO = "data_ora_inizio";
    private static final String COL_DATA_ORA_FINE = "data_ora_fine";


    @Override
    public java.util.List<ArrayList<String>> getEventiByMatricola(String matricola) {
        ArrayList<ArrayList<String>> eventi = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_EVENTI_BY_MATRICOLA_QUERY)) {

            stmt.setString(1, matricola);
            stmt.setString(2, matricola);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> evento = new ArrayList<>();
                    evento.add(rs.getString(COL_ID_AGENDA));
                    evento.add(rs.getString(COL_TITOLO));
                    evento.add(rs.getString(COL_DESCRIZIONE));

                    String matMed = rs.getString(COL_MATRICOLA_MEDICO);
                    String matAdmin = rs.getString(COL_MATRICOLA_AMMINISTRATORE);
                    evento.add(matMed != null ? matMed : matAdmin);

                    evento.add(rs.getString(COL_DATA_ORA_INIZIO));
                    evento.add(rs.getString(COL_DATA_ORA_FINE));
                    eventi.add(evento);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recuperare gli eventi per la matricola: " + matricola, e);
        }
        return eventi;
    }

    @Override
    public boolean addEvento(String titolo, String matricola, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine) {
        String query;
        boolean isAdmin = matricola != null && matricola.toUpperCase().startsWith("A");

        if (isAdmin) {
            query = ADD_EVENTO_ADMIN_QUERY;
        } else {
            query = ADD_EVENTO_MEDICO_QUERY;
        }

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, titolo);
            stmt.setString(2, descrizione);
            stmt.setTimestamp(3, dataOraInizio);
            stmt.setTimestamp(4, dataOraFine);
            stmt.setString(5, matricola);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento dell'evento nel database per la matricola: " + matricola, e);
            return false;
        }
    }

    @Override
    public boolean updateEvento(int idEvento, String titolo, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_EVENTO_QUERY)) {

            stmt.setString(1, titolo);
            stmt.setString(2, descrizione);
            stmt.setTimestamp(3, dataOraInizio);
            stmt.setTimestamp(4, dataOraFine);
            stmt.setInt(5, idEvento);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'evento nel database", e);
            return false;
        }
    }

    @Override
    public boolean deleteEvento(int idEvento) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_EVENTO_QUERY)) {
            stmt.setInt(1, idEvento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione dell'evento dal database", e);
            return false;
        }
    }

    @Override
    public boolean creaAgendaPerMedico(String matricolaMedico) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREA_AGENDA_QUERY)) {
            stmt.setString(1, matricolaMedico);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la creazione automatica dell'agenda per il medico", e);
            return false;
        }
    }

    @Override
    public boolean creaAgendaPerAmministratore(String matricolaAmministratore) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREA_AGENDA_ADMIN_QUERY)) {
            stmt.setString(1, matricolaAmministratore);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la creazione automatica dell'agenda per l'amministratore", e);
            return false;
        }
    }
}