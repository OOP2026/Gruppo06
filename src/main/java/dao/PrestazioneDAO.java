package dao;

import java.util.ArrayList;

public interface PrestazioneDAO {
    boolean aggiungiPrestazione(String tipologiaPrestazione, String esitoPrestazione, String idTurno, String cfPaziente, String matricolaMedico, String idAgenda);
    ArrayList<ArrayList<String>> getAllPrestazioni();
    ArrayList<ArrayList<String>> getPrestazioniByMedico(String matricola);
    ArrayList<String> getPrestazioneById(String idPrestazione);
    boolean updatePrestazione(int idPrestazione, String tipologia, String esito, String referto);
    boolean eliminaPrestazione(int idPrestazione);
}