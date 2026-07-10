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

public class MedicoPostgresDAO implements MedicoDAO {

    private static final Logger LOGGER = Logger.getLogger(MedicoPostgresDAO.class.getName());
    
    private static final String AGGIUNGI_MEDICO_QUERY = "INSERT INTO medico (nome, cognome, matricola, login, password, iscrizione_albo, specializzazione, reparto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_MEDICO_BY_MATRICOLA_QUERY = "SELECT nome, cognome, login, password, matricola, iscrizione_albo, specializzazione, reparto FROM medico WHERE matricola = ?";
    private static final String GET_ALL_MEDICI_QUERY = "SELECT nome, cognome, login, password, matricola, iscrizione_albo, specializzazione, reparto FROM medico WHERE matricola LIKE 'M%'";
    private static final String GET_MEDICO_BY_LOGIN_AND_PASSWORD_QUERY = "SELECT nome, cognome, login, password, matricola, iscrizione_albo, specializzazione, reparto FROM medico WHERE login = ? AND password = ?";
    private static final String CHECK_LOGIN_ESISTENTE_QUERY = "SELECT 1 FROM medico WHERE login = ?";
    private static final String AGGIORNA_MEDICO_QUERY = "UPDATE medico SET nome = ?, cognome = ?, iscrizione_albo = ?, specializzazione = ?, reparto = ? WHERE matricola = ?";
    private static final String COL_NOME = "nome";
    private static final String COL_COGNOME = "cognome";
    private static final String COL_LOGIN = "login";
    private static final String COL_PASSWORD = "password";
    private static final String COL_MATRICOLA = "matricola";
    private static final String COL_ISCRIZIONE_ALBO = "iscrizione_albo";
    private static final String COL_SPECIALIZZAZIONE = "specializzazione";
    private static final String COL_REPARTO = "reparto";

    @Override
    public boolean aggiungiMedico(String nome, String cognome, String matricola, String login, String password, String iscrizioneAlbo, String specializzazione, String reparto) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIUNGI_MEDICO_QUERY)) {

            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, matricola);
            stmt.setString(4, login);
            stmt.setString(5, password);
            
            if (iscrizioneAlbo != null && !iscrizioneAlbo.trim().isEmpty()) {
                stmt.setDate(6, java.sql.Date.valueOf(iscrizioneAlbo));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            stmt.setString(7, specializzazione);
            stmt.setString(8, reparto);
            return stmt.executeUpdate() > 0; 
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del medico", e);
            return false;
        }
    }


    @Override
    public ArrayList<String> getMedicoByMatricola(String matricola) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_MEDICO_BY_MATRICOLA_QUERY)) {
            stmt.setString(1, matricola);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMedicoFromResultSet(rs);
                }
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del medico per matricola", e);
        }
        return new ArrayList<>();
    }
    @Override
    public ArrayList<ArrayList<String>> getAllMedici() {
        ArrayList<ArrayList<String>> medici = new ArrayList<>();
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_MEDICI_QUERY);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                medici.add(extractMedicoFromResultSet(rs));
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero di tutti i medici", e);
        }
        return medici;
    }

    @Override
    public boolean aggiornaMedico(String nome, String cognome, String matricola, String iscrizioneAlbo, String specializzazione, String reparto) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(AGGIORNA_MEDICO_QUERY)) {
            stmt.setString(1, nome);
            stmt.setString(2, cognome);

            if (iscrizioneAlbo != null && !iscrizioneAlbo.trim().isEmpty()) {
                stmt.setDate(3, java.sql.Date.valueOf(iscrizioneAlbo));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setString(4, specializzazione);
            stmt.setString(5, reparto);
            stmt.setString(6, matricola);

            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento del medico", e);
        }
        return false;
    }

    @Override
    public boolean eliminaMedico(String matricola) {
        // Tipicamente è meglio disattivare l'utente piuttosto che eliminarlo fisicamente
        return false;
    }

    @Override
    public ArrayList<String> getMedicoByLoginAndPassword(String login, String password) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_MEDICO_BY_LOGIN_AND_PASSWORD_QUERY)) {

            stmt.setString(1, login);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMedicoFromResultSet(rs);
                }
            }
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del medico per login e password", e);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean checkLoginEsistente(String login) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(CHECK_LOGIN_ESISTENTE_QUERY)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Ritorna true se trova una corrispondenza
            }

        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la verifica del login del medico", e);
        }
        return false;
    }

    private ArrayList<String> extractMedicoFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<String> datiMedico = new ArrayList<>();
        datiMedico.add(rs.getString(COL_NOME));
        datiMedico.add(rs.getString(COL_COGNOME));
        datiMedico.add(rs.getString(COL_LOGIN));
        datiMedico.add(rs.getString(COL_PASSWORD));
        datiMedico.add(rs.getString(COL_MATRICOLA));

        java.sql.Date dataIscrizione = rs.getDate(COL_ISCRIZIONE_ALBO);
        datiMedico.add(dataIscrizione != null ? dataIscrizione.toString() : "");

        datiMedico.add(rs.getString(COL_SPECIALIZZAZIONE));
        datiMedico.add(rs.getString(COL_REPARTO));
        return datiMedico;
    }
}